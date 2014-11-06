package com.prezi.spaghetti.kotlin

import com.prezi.spaghetti.ast.InterfaceNode
import com.prezi.spaghetti.ast.InterfaceReferenceBase
import com.prezi.spaghetti.ast.MethodNode

class KotlinInterfaceMethodGeneratorVisitor extends AbstractKotlinMethodGeneratorVisitor {

    private final String interfaceName
    private final Set<InterfaceReferenceBase> superInterfaces

    KotlinInterfaceMethodGeneratorVisitor(String interfaceName, Set<InterfaceReferenceBase> superInterfaces) {
        this.interfaceName = interfaceName
        this.superInterfaces = collect(superInterfaces)

        println("interface ${interfaceName} has ${this.superInterfaces.size()} superinterfaces (${superInterfaces.size()})")
    }

    private Set<InterfaceReferenceBase> collect(Set<InterfaceReferenceBase> superInterfaces) {
        Set<InterfaceReferenceBase> result = new HashSet<>()
        for (InterfaceReferenceBase ifaceRef : superInterfaces) {
            result.add(ifaceRef)
            result.addAll(collect(ifaceRef.type.superInterfaces))
        }
        return result
    }

    @Override
    boolean isOverridden(MethodNode node) {
        // Members of kotlin.Any
        if (node.name == "equals" ||
            node.name == "hashCode" ||
            node.name == "toString")
            return true

        for (InterfaceReferenceBase ifaceRef : superInterfaces) {
            def iface = ifaceRef.type

            println("looking for ${node.name} in ${iface.name}")
            if (iface.methods.contains(node.name)) {
                println("${node.name} will be overridden")
                return true
            }
        }
        return false
    }
}
