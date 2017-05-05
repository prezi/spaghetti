/// <reference types="node" />

import * as ts from "typescript";
import * as fs from 'fs';


class Linter {
    private sourceFile: ts.SourceFile;
    private errors: Array<string> = [];

    constructor(sourceFile: ts.SourceFile) {
        this.sourceFile = sourceFile;
    }

    hasErrors(): boolean {
        return this.errors.length > 0;
    }

    printErrors() {
        this.errors.forEach((e) => process.stdout.write(e + '\n'));
    }

    lintError(message: string, node: ts.Node) {
        let { line, character } = this.sourceFile.getLineAndCharacterOfPosition(node.getStart());
        let output = `${this.sourceFile.fileName} (${line + 1},${character + 1}): ${message}`;
        this.errors.push(output);
    }

    lint() {
        const sourceFile = this.sourceFile;
        if (sourceFile.amdDependencies.length > 0) {
            this.lintError("Amd dependencies are not allowed", sourceFile);
        }
        if (sourceFile.statements.length > 1) {
            this.lintError("Expecting only one module declaration at the top level.", sourceFile);
            return;
        }
        if (sourceFile.statements.length === 0
            || sourceFile.statements[0].kind !== ts.SyntaxKind.ModuleDeclaration) {
            this.lintError("Expecting a single module declaration at the top level.", sourceFile);
            return;
        }

        var statement: ts.Node = sourceFile.statements[0];
        while (statement.kind === ts.SyntaxKind.ModuleDeclaration) {
            var body = (<ts.ModuleDeclaration>statement).body
            if (body == null) {
                break;
            }
            statement = body;
        }

        if (statement.kind !== ts.SyntaxKind.ModuleBlock) {
            this.lintError("Expecting a module block inside top level module declaration", sourceFile);
            return;
        }

        ts.forEachChild(statement, (n) => this.lintStatements(n));
    }

    lintVariableDeclaration(node: ts.VariableDeclaration) {
        if (node.type == null) {
            this.lintError("Variables without explicit types are not allowed.", node);
        }
        else if (node.type.kind === ts.SyntaxKind.AnyKeyword) {
            this.lintError("Variables should not have 'any' type.", node);
        }
    }

    lintStatements(node: ts.Node) {
        if (!isNodeExported(node)) {
            return;
        }

        this.lintNode(node);
    }

    lintNode(node: ts.Node) {
        switch (node.kind) {
            case ts.SyntaxKind.VariableStatement:
                let varDecl = (<ts.VariableStatement>node).declarationList;
                varDecl.declarations.forEach((n) => this.lintVariableDeclaration(n));
                if (!(varDecl.flags & ts.NodeFlags.Const)) {
                    this.lintError("'var' and 'let' are not allowed. Please use 'const' instead.", node);
                }
                break;

            case ts.SyntaxKind.ClassDeclaration:
                this.lintError("Classes are not allowed. Use a factory function and an interface instead.", node);
                break;

            case ts.SyntaxKind.ModuleBlock:
                ts.forEachChild(node, (n) => this.lintStatements(n))
                break;

            case ts.SyntaxKind.Block:
                if (node.parent && node.parent.kind === ts.SyntaxKind.FunctionDeclaration) {
                    break;
                } else {
                    ts.forEachChild(node, (n) => this.lintNode(n));
                }
                break;

            default:
                ts.forEachChild(node, (n) => this.lintNode(n));
        }
    }
}

function hasModifier(node: ts.Node, kind: ts.SyntaxKind) {
    return node.modifiers != null && node.modifiers.some(
            (modifier: ts.Modifier) => modifier.kind === kind);
}

function isNodeExported(node: ts.Node): boolean {
    if (hasModifier(node, ts.SyntaxKind.ExportKeyword)) {
        return true;
    }

    let ancestor: ts.Node = node;
    while (ancestor.parent != null) {
        ancestor = ancestor.parent;
        if (ancestor.kind === ts.SyntaxKind.ModuleDeclaration
                && hasModifier(ancestor, ts.SyntaxKind.DeclareKeyword)) {
            return true;
        }
    }

    return false;
}


function getProtectedIdentifiers(sourceFile: ts.SourceFile) {
    const idents: { [key: string]: string } = {};

    function visitAst(node: ts.Node) {
        switch (node.kind) {
            case ts.SyntaxKind.Parameter:
            case ts.SyntaxKind.TypeParameter:
            case ts.SyntaxKind.TypeReference:
            case ts.SyntaxKind.TypeAliasDeclaration:
                break;

            case ts.SyntaxKind.Identifier:
                if (node.parent && node.parent.kind === ts.SyntaxKind.InterfaceDeclaration) {
                    break;
                }
                let text = (<ts.Identifier>node).text;
                idents[text] = text;
                break;

            default:
                ts.forEachChild(node, visitAst);
        }
    }

    ts.forEachChild(sourceFile, visitAst);

    let list = Object.keys(idents);
    list.sort();
    return list;
}

function getSourceFile(filename: string) {
    let sourceFile = ts.createSourceFile(
            filename,
            fs.readFileSync(filename, 'utf8'),
            ts.ScriptTarget.ES5,
            true);
    let parseErrors: Array<ts.Diagnostic> = (<any>sourceFile).parseDiagnostics;
    if (parseErrors && parseErrors.length > 0) {
        parseErrors.forEach((error: ts.Diagnostic) => {
            let { line, character } = sourceFile.getLineAndCharacterOfPosition(error.start);
            let message = ts.flattenDiagnosticMessageText(error.messageText, "\n");
            let output = `${sourceFile.fileName} (${line + 1},${character + 1}): ${message}`;
            process.stderr.write(output + '\n');
        });
        process.exit(2);
    }
    return sourceFile;
}

const args = process.argv.slice(2);
if (args[0] === "--collectExportedIdentifiers") {
    let filename = args[1];
    let sourceFile = getSourceFile(filename);
    let idents = getProtectedIdentifiers(sourceFile);
    process.stdout.write(idents.join(',') + '\n');
    process.exit(0);

} else if (args[0] === "--verifyModuleDefinition") {
    let filename = args[1];
    let sourceFile = getSourceFile(filename);
    let linter = new Linter(sourceFile);
    linter.lint();
    if (linter.hasErrors()) {
        linter.printErrors();
        process.exit(1);
    } else {
        process.exit(0);
    }
}
