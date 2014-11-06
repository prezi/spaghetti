package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.InterfaceReferenceBase
import com.prezi.spaghetti.ast.MethodNode

class KotlinInterfaceMethodGeneratorVisitor extends AbstractKotlinMethodGeneratorVisitor {

    private final Set<InterfaceReferenceBase> superInterfaces

    KotlinInterfaceMethodGeneratorVisitor(Set<InterfaceReferenceBase> superInterfaces) {
        this.superInterfaces = collect(superInterfaces)
    }

    private Set<InterfaceReferenceBase> collect(Set<InterfaceReferenceBase> superInterfaces) {
        Set<InterfaceReferenceBase> result = new HashSet<>()
        for (InterfaceReferenceBase iface : superInterfaces) {
            result.addAll(collect(iface.superInterfaces))
        }
        return result
    }

    @Override
    boolean isOverridden(MethodNode node) {
        for (InterfaceReferenceBase ifaceRef : superInterfaces) {
            def iface = ifaceRef.type
            if (iface.methods.contains(node.name)) {
                return true
            }
        }
        return false
    }
}
