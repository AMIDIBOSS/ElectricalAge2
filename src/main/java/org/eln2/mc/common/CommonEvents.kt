package org.eln2.mc.common

import net.minecraft.server.level.ServerLevel
import net.minecraftforge.event.ServerChatEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.server.ServerLifecycleHooks
import org.eln2.mc.Eln2
import org.eln2.mc.common.cell.CellGraphManager

@Mod.EventBusSubscriber
object CommonEvents {
    @SubscribeEvent
    fun onServerTick(event : TickEvent.ServerTickEvent){
        if(event.phase == TickEvent.Phase.END){
            ServerLifecycleHooks.getCurrentServer().allLevels.forEach{
                CellGraphManager.getFor(it).tickAll()
            }
        }
    }

    @SubscribeEvent
    fun onChat(event : ServerChatEvent){
        when (event.message) {
            "build" -> {
                CellGraphManager.getFor(event.player.level as ServerLevel).graphs.values.forEach{ it.build() }
            }
            "circuits" -> {
                CellGraphManager.getFor(event.player.level as ServerLevel).graphs.values.forEach {
                    Eln2.LOGGER.info("Circuit:")
                    Eln2.LOGGER.info(
                        it.circuit.components.map{
                                comp ->  "\n    ${comp.detail()}${comp.pins.map { pin -> pin.node?.index}}"
                        }
                    )
                }
            }
        }
    }
}
