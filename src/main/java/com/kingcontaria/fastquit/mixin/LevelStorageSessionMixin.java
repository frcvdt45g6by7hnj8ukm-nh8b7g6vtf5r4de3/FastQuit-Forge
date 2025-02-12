package com.kingcontaria.fastquit.mixin;

public abstract class LevelStorageSessionMixin {

//    @Shadow @Final private String levelId;
//
//    @Synchronized
//    @Shadow public abstract PlayerData createPlayerStorage();
//
//    @Synchronized
//    @Shadow public abstract @Nullable WorldSummary getSummary();
//
//    @Synchronized
//    @Shadow public abstract @Nullable IServerConfiguration getDataTag(DynamicOps<INBT> pNbt, DatapackCodec pDatapackCodec);
//    @Synchronized
//    @Shadow public abstract @Nullable DatapackCodec getDataPacks();
//
//    @Synchronized
//    @Shadow public abstract void saveDataTag(DynamicRegistries pRegistries, IServerConfiguration pServerConfiguration, @Nullable CompoundNBT pHostPlayerNBT);
//
//    @Synchronized
//    @Shadow public abstract void deleteLevel() throws IOException;
//
//    @Synchronized
//    @Shadow public abstract void renameLevel(String name) throws IOException;
//
//    @Synchronized
//    @Shadow public abstract void close() throws IOException;


    // this now acts as a fallback in case the method gets called from somewhere else than EditWorldScreen
    // 现在它充当回退机制，以防方法从 EditWorldScreen 以外的地方被调用。

//    @Inject(method = "makeWorldBackup", at = @At("HEAD"))
//    private void fastquit$waitForSaveOnBackup(CallbackInfoReturnable<Long> cir) {
//        SaveManager.getSavingWorld((ISaveFormat) (Object) this).ifPresent(SaveManager::wait);
//    }





}