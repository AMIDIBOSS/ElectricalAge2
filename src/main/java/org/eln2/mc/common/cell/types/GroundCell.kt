package org.eln2.mc.common.cell.types

import net.minecraft.core.BlockPos
import org.ageseries.libage.sim.electrical.mna.component.Resistor
import org.eln2.mc.common.cell.CellBase
import org.eln2.mc.common.cell.ComponentInfo
import org.eln2.mc.extensions.ComponentExtensions.connectToPinOf

class GroundCell(pos : BlockPos) : CellBase(pos) {

    /*  R -> local resistors. Their first pin is grounded.
    *   C -> remote components. The second pin of the local resistors is used for them.
    *
    *       C
    *       R
    *   C R G R C
    *       R
    *       C
    */

    override fun clearForRebuild() {
        neighbourToResistorLookup.clear()
    }

    override fun componentForNeighbour(neighbour: CellBase): ComponentInfo {
        val circuit = graph.circuit

        return ComponentInfo(neighbourToResistorLookup.computeIfAbsent(neighbour) {
            val resistor = Resistor()
            resistor.resistance = 0.001
            circuit.add(resistor)
            resistor
        }, 1) // pin 1 will be connected to the remote components
    }

    override fun buildConnections() {
        connections.forEach{ adjacentCell ->
            val resistor = componentForNeighbour(adjacentCell).component
            resistor.ground(0) // ground one pin of our resistor

            // then connect the other pin to them
            resistor.connectToPinOf(1, adjacentCell.componentForNeighbour(this))
        }
    }

    private val neighbourToResistorLookup = HashMap<CellBase, Resistor>()

    // todo: bogus
    override fun createDataPrint(): String {
        val current = connections.sumOf { (componentForNeighbour(it).component as Resistor).current }
        return "I: $current"
    }
}
