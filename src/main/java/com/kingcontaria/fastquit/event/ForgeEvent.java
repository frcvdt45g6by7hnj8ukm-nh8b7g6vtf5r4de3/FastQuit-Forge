package com.kingcontaria.fastquit.event;

import com.kingcontaria.fastquit.FastQuit;
import com.kingcontaria.fastquit.mixin.accessor.EditWorldScreenAccessor;
import com.kingcontaria.fastquit.util.ModLogger;
import com.kingcontaria.fastquit.util.SaveManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//@Mod.EventBusSubscriber(modid = FastQuit.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeEvent {
//    @SubscribeEvent
//    public static void waitForSaveOnOptimize(ScreenEvent.InitScreenEvent.Pre event){
//        if (event.getScreen() instanceof BackupConfirmScreen){
//            EditWorldScreenAccessor accessor = (EditWorldScreenAccessor) editWorldScreen;
//            CallbackInfo ci = new CallbackInfo("lambda$init$6", true);
//            SaveManager.getSavingWorld(accessor.getLevelAccess()).ifPresent(server -> SaveManager.wait(server, ci));
//        }
//    }
}
