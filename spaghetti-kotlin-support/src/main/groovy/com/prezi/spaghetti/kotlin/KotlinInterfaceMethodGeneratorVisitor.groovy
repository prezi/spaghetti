package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.MethodNode

class KotlinInterfaceMethodGeneratorVisitor extends AbstractKotlinMethodGeneratorVisitor {

    private final Set<InterfaceNode> superInterfaces

    KotlinInterfaceMethodGeneratorVisitor(Set<InterfaceNode> superInterfaces) {
        this.superInterfaces = collect(superInterfaces)
    }

    private Set<InterfaceNode> collect(Set<InterfaceNode> superInterfaces) {
        Set<InterfaceNode> result = new HashSet<>()
        for (InterfaceNode iface : superInterfaces) {
            result.addAll(collect(iface.superInterfaces))
        }
        return result
    }

    @Override
    boolean isOverridden(MethodNode node) {
        for (InterfaceNode iface : superInterfaces) {
            if (iface.methods.contains(node.name)) {
                return true
            }
        }
        return false
    }
}
