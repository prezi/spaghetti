/// <reference types="node" />

import * as ts from "typescript";
import * as fs from 'fs';
import * as path from 'path';

interface Replacement {
    start: number;
    end: number;
    replacementText: string;
}

interface ImportedIdentifiers {
    [key: string]: ImportType;
}

type ImportType = DefaultImport | StarImport | NamedImport | NamedWithAliasImport;

interface BaseImport {
    node: ts.ImportDeclaration;
    moduleSpec: string;
}

interface DefaultImport extends BaseImport {
    type: "default-import";
    name: string;
}

interface StarImport extends BaseImport {
    type: "star-import";
    name: string;
}

interface NamedImport extends BaseImport {
    type: "named-import";
    name: string;
}

interface NamedWithAliasImport extends BaseImport {
    type: "named-with-alias-import";
    name: string;
    propertyName: string;
}

class Linter {
    private sourceFile: ts.SourceFile;
    private errors: Array<string> = [];

    constructor(sourceFile: ts.SourceFile) {
        this.sourceFile = sourceFile;
    }

    hasErrors(): boolean {
        return this.errors.length > 0;
    }

    copyErrorsTo(other: Linter) {
        other.errors.push(...this.errors);
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
            this.lintError("Amd dependencies are not allowed.", sourceFile);
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
            this.lintError("Expecting a module block inside top level module declaration.", sourceFile);
            return;
        }

        ts.forEachChild(statement, (n) => this.lintStatements(n));
    }

    lintCommonJs(isSubModule: boolean = false) {
        const sourceFile = this.sourceFile;
        if (sourceFile.amdDependencies.length > 0) {
            this.lintError("Amd dependencies are not allowed.", sourceFile);
        }

        ts.forEachChild(sourceFile, (n) => this.lintStatements(n));

        const importedIdentifiers = collectImportedIdentifiers(sourceFile);

        ts.forEachChild(sourceFile, (node: ts.Node) => {
            if (isSubModule && isRelativeImportExport(node)) {
                if (node.kind === ts.SyntaxKind.ImportDeclaration) {
                    this.lintError("Relative imports are not permitted in file being merged.", node);
                } else if (node.kind === ts.SyntaxKind.ExportDeclaration) {
                    this.lintError("Exports from relative paths are not permitted in a file being merged.", node);
                }
            }

            if (!isSubModule && isRelativeImportExport(node) && node.kind === ts.SyntaxKind.ExportDeclaration) {
                const exportDecl = (<ts.ExportDeclaration>node);
                if (exportDecl.exportClause == null) {
                    // exportClause is null: 'export * from ...'
                    const filePath = getImportFilePath(sourceFile.fileName, exportDecl);
                    const subSourceFile = getSourceFile(filePath);
                    const subLinter = new Linter(subSourceFile);
                    subLinter.lintCommonJs(true);

                    const subImportedIds = collectImportedIdentifiers(subSourceFile);
                    Object.keys(subImportedIds).forEach((name: string) => {
                        if (importedIdentifiers[name] != null
                                && !importsCanBeMerged(importedIdentifiers[name], subImportedIds[name])) {
                            subLinter.lintError(`Duplicate imported identifier: '${name}'`, subImportedIds[name].node);
                        }
                    });

                    subLinter.copyErrorsTo(this);
                }
            }
        });
    }


    mergeCommonJsImports(): string | null {
        const importDeclarations: Array<ts.ImportDeclaration> = [];
        const exportModules: Array<string> = [];
        const statements: Array<ts.Statement> = [];
        ts.forEachChild(this.sourceFile, (node: ts.Node) => {
            if (isRelativeImportExport(node)) {
                if (node.kind === ts.SyntaxKind.ImportDeclaration) {
                    importDeclarations.push(node as ts.ImportDeclaration);
                    statements.push(node as ts.ImportDeclaration);
                } else if (node.kind === ts.SyntaxKind.ExportDeclaration) {
                    const exportDeclaration = (<ts.ExportDeclaration>node);
                    const moduleText = getImportExportText(exportDeclaration)!;
                    if (exportDeclaration.exportClause != null) {
                        this.lintError(`Named exports are not supported from relative modules: '${moduleText}'`, exportDeclaration);
                    } else {
                        exportModules.push(moduleText);
                        statements.push(exportDeclaration);
                    }
                }
            }
        });
        importDeclarations.forEach((importDeclaration) => {
            const moduleText = getImportText(importDeclaration)!;
            if (exportModules.indexOf(moduleText) === -1) {
                this.lintError(`Missing export * from '${moduleText}' statement.`, importDeclaration);
            }
        });

        if (this.hasErrors()) {
            return null;
        }

        const importedIdentifiers = collectImportedIdentifiers(this.sourceFile);
        const replacements: Array<Replacement> = [];
        statements.forEach((statement) => {
            let replacementText = "";
            if (statement.kind === ts.SyntaxKind.ExportDeclaration) {
                replacementText = getInlinedFileContent(this.sourceFile.fileName, importedIdentifiers, statement as ts.ExportDeclaration);
            }

            replacements.push({
                start: statement.getStart(),
                end: statement.getEnd(),
                replacementText: replacementText,
            });
        });

        const newText = replaceInText(this.sourceFile.text, replacements);

        return newText;
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

function getInlinedFileContent(filename: string, parentImportedIds: ImportedIdentifiers, exportDecl: ts.ExportDeclaration): string {
    const filePath = getImportFilePath(filename, exportDecl);
    const sourceFile = getSourceFile(filePath);

    const importedIds = collectImportedIdentifiers(sourceFile);
    const replacements: Array<Replacement> = [];
    const rewriteStatements: Array<ts.ImportDeclaration> = [];
    const removeIds: Array<NamedImport | NamedWithAliasImport> = [];
    Object.keys(importedIds).forEach((name: string) => {
        const id = importedIds[name];
        const parentId = parentImportedIds[name];
        if (parentId != null && importsCanBeMerged(id, parentId)) {
            if (id.type === "default-import" || id.type === "star-import") {
                replacements.push({
                    start: id.node.getStart(),
                    end: id.node.getEnd(),
                    replacementText: "",
                });
            } else if (id.type === "named-import" || id.type === "named-with-alias-import") {
                removeIds.push(id);
                if (rewriteStatements.indexOf(id.node) === -1) {
                    rewriteStatements.push(id.node);
                }
            }
        }
    });

    rewriteStatements.forEach(stmt => {
        const skipNames = removeIds.filter(id => id.node === stmt).map(id => id.name);

        const elements = (stmt.importClause!.namedBindings as ts.NamedImports).elements
            .filter(imp => {
                return skipNames.indexOf(imp.name.getText()) === -1
            })
            .map((imp: ts.ImportSpecifier) => {
                const name = imp.name.getText();
                if (imp.propertyName == null) {
                    return name;
                } else {
                    return `${imp.propertyName.getText()} as ${name}`;
                }
            })
            .join(", ");

        let text = "";
        if (elements.length > 0) {
            text = `import { ${elements} } from ${stmt.moduleSpecifier.getText()};`
        }

        replacements.push({
            start: stmt.getStart(),
            end: stmt.getEnd(),
            replacementText: text,
        });
    });


    const content = replaceInText(sourceFile.text, replacements);

    const exportSpecifier = exportDecl.moduleSpecifier ? exportDecl.moduleSpecifier.getText() : "";
    return [
        `/* Start of inlined export: ${exportSpecifier} */`,
        content,
        `/* End of inlined export: ${exportSpecifier} */`,
    ].join("\n");
}

function importsCanBeMerged(a: ImportType, b: ImportType) {
    if (a.type !== b.type) {
        return false;
    }

    if (a.type === "named-with-alias-import") {
        if ((a as NamedWithAliasImport).propertyName !== (b as NamedWithAliasImport).propertyName) {
            return false;
        }
    }

    return a.moduleSpec === b.moduleSpec && a.name === b.name;
}

function collectImportedIdentifiers(root: ts.Node): ImportedIdentifiers {
    const importedNames: ImportedIdentifiers = {};
    ts.forEachChild(root, (node: ts.Node) => {
        if ((node.kind === ts.SyntaxKind.ImportDeclaration
                && (<ts.ImportDeclaration>node).importClause != null)
                && !isRelativeImportExport(node)) {
            const importDecl = node as ts.ImportDeclaration;

            const clause = importDecl.importClause!;
            if (clause.name != null) {
                importedNames[clause.name.getText()] = {
                    type: "default-import",
                    node: importDecl,
                    moduleSpec: getImportText(importDecl),
                    name: clause.name.getText(),
                };
            }
            const bindings = clause.namedBindings;
            if (bindings != null && bindings.kind === ts.SyntaxKind.NamespaceImport) {
                const namespaceImport = bindings as ts.NamespaceImport;
                importedNames[namespaceImport.name.getText()] = {
                    type: "star-import",
                    node: importDecl,
                    moduleSpec: getImportText(importDecl),
                    name: namespaceImport.name.getText(),
                };

            } else if (bindings != null && bindings.kind === ts.SyntaxKind.NamedImports) {
                (bindings as ts.NamedImports).elements.forEach((imp: ts.ImportSpecifier) => {
                    if (imp.propertyName == null) {
                        importedNames[imp.name.getText()] = {
                            type: "named-import",
                            node: importDecl,
                            moduleSpec: getImportText(importDecl),
                            name: imp.name.getText(),
                        };
                    } else {
                        importedNames[imp.name.getText()] = {
                            type: "named-with-alias-import",
                            node: importDecl,
                            moduleSpec: getImportText(importDecl),
                            name: imp.name.getText(),
                            propertyName: imp.propertyName.getText(),
                        };
                    }
                });
            }
        }
    });
    return importedNames;
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

function isRelativeImportExport(decl: ts.Node): boolean {
    const relativePathMatch = /^\.?\.?\//;
    if (decl.kind === ts.SyntaxKind.ImportDeclaration
            ||decl.kind === ts.SyntaxKind.ExportDeclaration) {
        const moduleText = getImportExportText(decl as (ts.ImportDeclaration | ts.ExportDeclaration));
        if (moduleText != null && moduleText.match(relativePathMatch) != null) {
            return true;
        }
    }
    return false;
}

function getImportFilePath(parentPath: string, exportDeclaration: ts.ExportDeclaration) {
    const importPath = getImportExportText(exportDeclaration);
    const relativePath = importPath + ".d.ts";
    return path.resolve(path.dirname(parentPath), relativePath);
}

function getImportText(decl: ts.ImportDeclaration): string {
    return decl.moduleSpecifier.getText().replace(/['"]/g, '');
}

function getImportExportText(declaration: ts.ImportDeclaration | ts.ExportDeclaration): string | null {
    if (declaration.moduleSpecifier != null) {
        return declaration.moduleSpecifier.getText().replace(/['"]/g, '');
    }
    return null;
}

function replaceInText(sourceText: string, replacements: Replacement[]) {
    replacements.sort((a, b) => {
        // sort into reverse order
        // (assuming the ranges don't overlap)
        return a.start < b.start ? 1 : -1;
    });
    for (const { start, end, replacementText } of replacements) {
        sourceText = sourceText.slice(0, start) + replacementText + sourceText.slice(end)
    }

    return sourceText;
}



const args = process.argv.slice(2);
if (args[0] === "--collectExportedIdentifiers") {
    const filename = args[1];
    const sourceFile = getSourceFile(filename);
    const idents = getProtectedIdentifiers(sourceFile);
    process.stdout.write(idents.join(',') + '\n');
    process.exit(0);

} else if (args[0] === "--verifyModuleDefinition") {
    const filename = args[1];
    const sourceFile = getSourceFile(filename);
    const linter = new Linter(sourceFile);
    linter.lint();
    if (linter.hasErrors()) {
        linter.printErrors();
        process.exit(1);
    } else {
        process.exit(0);
    }
} else if (args[0] === "--mergeDtsForJs") {
    const filename = args[1];
    const sourceFile = getSourceFile(filename);
    const linter = new Linter(sourceFile);
    linter.lintCommonJs();
    const lintedFileText = linter.mergeCommonJsImports();
    if (linter.hasErrors()) {
        linter.printErrors();
        process.exit(1);
    } else {
        fs.writeFileSync(args[2], lintedFileText, { encoding: "utf8" });
        process.exit(0);
    }
}
