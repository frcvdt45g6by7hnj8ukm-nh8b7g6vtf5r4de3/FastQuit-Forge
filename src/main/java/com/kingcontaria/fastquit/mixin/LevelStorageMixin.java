package com.kingcontaria.fastquit.mixin;

public abstract class LevelStorageMixin {


//    @Inject(method = "createAccess", at = @At("HEAD"))
//    private void fastquit$waitForSaveOnSessionCreation(String pSaveName, CallbackInfoReturnable<SaveFormat.LevelSave> cir) {
//        if (!ModConfigManager.getConfig().allowMultipleServers()) {
//            SaveManager.wait(SaveManager.savingWorlds.keySet());
//        }
//        SaveManager.getSavingWorld(this.baseDir.resolve(pSaveName)).ifPresent(SaveManager::wait);
//    }

//    @Inject(method = "checkSessionLock", at = @At(value = "CONSTANT", args = "stringValue=Failed to check session lock, aborting"))
//    private void fastquit$addCurrentlySavingLevelsToWorldList(CallbackInfo cir, @Local File file1) {
//        SaveManager.getSession(file1.toPath()).ifPresent(session -> {
//            try {
//                worldSummaries.add(session.getSummary());
////                cir.setReturnValue(worldSummaries);
//            } catch (Exception e) {
//                ModLogger.error("Failed to load level summary from saving server!", e);
//            } finally {
//                try {
//                    session.close(); // 手动关闭
//                } catch (Exception e) {
//                    ModLogger.error("Failed to close session!", e);
//                }
//            }
//        });
//    }
}