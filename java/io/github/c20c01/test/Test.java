package io.github.c20c01.test;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

@Mod(Test.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Test.MODID)
public class Test {
    static final String MODID = "ccmod";
    private static final Minecraft mc = Minecraft.getInstance();
    private static boolean attackDown = false;
    private static boolean doingJob = false;
    private static Timer timer;
    private static boolean first = true;
    private static boolean customModeFirst = true;
    private static boolean set = false;
    private static BlockPos setPoint = null;
    private static int base_x, base_y, base_z;
    private static int x = 0;
    private static int z = 0;
    private static int longX = 8;
    private static int longZ = 8;
    private static boolean positive = true;
    private static int number = 0;
    private static final int[] BLOCK = new int[16384];
    private static final int[] HAVE_BLOCK = new int[16384];
    private static boolean pause = false;
    private static int settingCode = 0;
    private static boolean hadFile = false;
    private static boolean cancelOrStop = false;
    private static boolean stop = false;
    private static boolean arrive = false;
    private static boolean customMode = true;
    private static int haveBlockI = 0;
    private static int maxNumber = 64;

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (mc.level != null) if (mc.player != null && mc.options.keyAttack.isDown()) {
            if (!attackDown) {
                if (mc.player.getMainHandItem().getItem() == Items.FEATHER) if (doingJob) {
                    if (stop) {
                        stop = false;
                        cancelOrStop = false;
                        mc.player.sendMessage(Component.nullToEmpty("Resume!"), mc.player.getUUID());
                    }
                } else if (mc.hitResult != null && mc.hitResult.getType().equals(HitResult.Type.BLOCK)) {
                    if (set) {
                        DoJob();
                        set = false;
                    } else SetPoint();
                } else if (!hadFile) LoadingMap();
                else {
                    hadFile = false;
                    mc.player.sendMessage(Component.nullToEmpty("The map file is no longer used!"), mc.player.getUUID());
                }
                if (mc.player.getMainHandItem().getItem() == Items.STICK) if (doingJob) {
                    if (!cancelOrStop) {
                        stop = true;
                        cancelOrStop = true;
                        assert mc.level != null;
                        mc.player.playSound(SoundEvents.NOTE_BLOCK_PLING, 10, 10);
                        mc.player.sendMessage(Component.nullToEmpty("Pause! Click with feather to resume,or click with stick again to cancel."), mc.player.getUUID());
                    } else {
                        stop = false;
                        cancelOrStop = false;
                        EndJob();
                        mc.player.sendMessage(Component.nullToEmpty("Cancel!"), mc.player.getUUID());
                    }
                } else if (mc.hitResult != null && mc.hitResult.getType().equals(HitResult.Type.BLOCK)) CrashRestart();
                else Setting();
                attackDown = true;
            }
        } else attackDown = false;
    }

    public static void GOGO() {
        assert mc.player != null;
        timer.schedule(new TimerTask() {
            public void run() {
                if (mc.level == null) timer.cancel();
                if (!stop) {
                    if (!hadFile) ChangeBlock();
                    if (mc.player.getMainHandItem().getItem() == Items.AIR || mc.player.getMainHandItem().getItem() == Items.STICK || mc.player.getMainHandItem().getItem() == Items.FEATHER) {
                        if (!pause) {
                            mc.player.sendMessage(Component.nullToEmpty("Pause! Get some blocks in your hand to resume."), mc.player.getUUID());
                            pause = true;
                        }
                    } else {
                        pause = false;
                        if (!hadFile) {
                            if (doingJob) SetAndRun();
                        } else {
                            mc.player.moveTo(base_x + 0.5 + x, base_y + 2, base_z + 0.5 + z);
                            WantGo();
                            if (arrive) {
                                ChooseAndSet();
                                arrive = false;
                                if (haveBlockI >= 16383 || HAVE_BLOCK[haveBlockI] > maxNumber || HAVE_BLOCK[haveBlockI] == 0) {
                                    EndJob();
                                    mc.player.sendMessage(Component.nullToEmpty("Building completed according to the map!"), mc.player.getUUID());
                                }
                            }
                        }
                    }
                }
            }
        }, 0, 200);
    }

    public static void DoJob() {
        assert mc.player != null;
        assert mc.gameMode != null;
        doingJob = true;
        timer = new Timer();
        mc.player.sendMessage(Component.nullToEmpty("Start in 5s."), mc.player.getUUID());
        timer.schedule(new TimerTask() {
            public void run() {
                GOGO();
                assert mc.level != null;
                mc.player.playSound(SoundEvents.NOTE_BLOCK_PLING, 10, 10);
            }
        }, 5000);
    }

    public static void SetAndRun() {
        SetBlock(base_x + x, base_y + 1, base_z + z);
        assert mc.player != null;
        mc.player.moveTo(base_x + 0.5 + x, base_y + 2, base_z + 0.5 + z);
        if (positive) {
            if (x < longX - 1) x++;
            else if (z < longZ - 1) {
                z++;
                positive = false;
            } else {
                EndJob();
                mc.player.sendMessage(Component.nullToEmpty("Done!"), mc.player.getUUID());
            }
        } else if (x > 0) x--;
        else if (z < longZ - 1) {
            z++;
            positive = true;
        } else {
            EndJob();
            mc.player.sendMessage(Component.nullToEmpty("Done!"), mc.player.getUUID());
        }
    }

    public static void WantGo() {
        assert mc.player != null;
        number = HAVE_BLOCK[haveBlockI];
        int xDis = number / longX - x;
        int zDis = number % longX - z;
        if (xDis == 0 && zDis == 0) {
            arrive = true;
            haveBlockI++;
        } else {
            x += (xDis) / 4 == 0 ? (xDis) % 4 : (xDis > 0 ? 4 : -4);
            z += (zDis) / 4 == 0 ? (zDis) % 4 : (zDis > 0 ? 4 : -4);
        }
    }

    public static void ChooseAndSet() {
        assert mc.player != null;
        int BlockNumber = BLOCK[number] - 48;
        if (BlockNumber > -1 && BlockNumber < 9) mc.player.getInventory().selected = BlockNumber;
        SetBlock(base_x + x, base_y + 1, base_z + z);
    }

    public static void ChangeBlock() {
        if (pause) {
            assert mc.player != null;
            if (mc.player.getMainHandItem().getItem() == Items.AIR)
                if (mc.player.getInventory().selected < 8) mc.player.getInventory().selected++;
                else {
                    mc.player.getInventory().selected = 0;
                    assert mc.level != null;
                    mc.player.playSound(SoundEvents.NOTE_BLOCK_PLING, 10, 10);
                    mc.player.sendMessage(Component.nullToEmpty("No Block in bar!"), mc.player.getUUID());
                }
        }
    }

    public static void GetBase() {
        base_x = setPoint.getX();
        base_y = setPoint.getY();
        base_z = setPoint.getZ();
    }

    public static void SetPoint() {
        assert mc.hitResult != null;
        if (first) {
            set = false;
            setPoint = ((BlockHitResult) mc.hitResult).getBlockPos();
            assert mc.player != null;
            mc.player.sendMessage(Component.nullToEmpty("Do it again to confirm the reference point."), mc.player.getUUID());
            first = false;
        } else if (((BlockHitResult) mc.hitResult).getBlockPos().equals(setPoint)) {
            if (!customMode) {
                GetBase();
                assert mc.player != null;
                mc.player.sendMessage(Component.nullToEmpty("Hit any block with feather to start building."), mc.player.getUUID());
                set = true;
            } else if (customModeFirst) {
                GetBase();
                assert mc.player != null;
                mc.player.sendMessage(Component.nullToEmpty("Hit other block with feather to mark the second point(only x,z)."), mc.player.getUUID());
                customModeFirst = false;
            } else {
                longX = setPoint.getX() - base_x + 1;
                longZ = setPoint.getZ() - base_z + 1;
                if (((longX >= 1) && (longX <= 128)) && ((longZ >= 1) && (longZ <= 128))) {
                    maxNumber = longX * longZ;
                    assert mc.player != null;
                    mc.player.sendMessage(Component.nullToEmpty("Go back to the first point\nand hit any block with feather to start building."), mc.player.getUUID());
                    set = true;
                } else {
                    assert mc.player != null;
                    if (longX < 1 || longZ < 1)
                        mc.player.sendMessage(Component.nullToEmpty("The second point must be southwest(x++,z++) of the first point!\nSet the first and second point again."), mc.player.getUUID());
                    else
                        mc.player.sendMessage(Component.nullToEmpty("The area must smaller than 128*128!\nSet the first and second point again."), mc.player.getUUID());
                    set = false;
                }
                first = true;
                customModeFirst = true;
            }
        } else {
            first = true;
            SetPoint();
        }
    }

    public static void LoadingMap() {
        assert mc.player != null;
        boolean HadDir;
        String path = ".\\CCMod";
        File dir = new File(path);
        File file = new File(path + "\\file.txt");
        HadDir = !dir.mkdir();
        if (!HadDir)
            mc.player.sendMessage(Component.nullToEmpty("A new folder named 'CCMod' has been created in the game folder."), mc.player.getUUID());
        try {
            hadFile = !file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (hadFile) {
            Reader reader;
            try {
                reader = new InputStreamReader(new FileInputStream(file));
                int i = 0;
                int j = 0;
                int tempChar;
                while ((tempChar = reader.read()) != -1) if (((char) tempChar) != '\n' && ((char) tempChar) != '\r') {
                    BLOCK[i] = (char) tempChar;
                    if (BLOCK[i] - 48 > -1 && BLOCK[i] - 48 < 9) {
                        HAVE_BLOCK[j] = i;
                        j++;
                    }
                    i++;
                }
                reader.close();
                mc.player.sendMessage(Component.nullToEmpty("The map file is loaded. Click with stick to set the target size."), mc.player.getUUID());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            mc.player.sendMessage(Component.nullToEmpty("A new folder named 'file.txt' has been created in 'CCMod'."), mc.player.getUUID());
    }

    public static void EndJob() {
        assert mc.level != null;
        assert mc.player != null;
        timer.cancel();
        x = 0;
        z = 0;
        number = 0;
        doingJob = false;
        positive = true;
        pause = false;
        cancelOrStop = false;
        stop = false;
        arrive = false;
        haveBlockI = 0;
        mc.player.playSound(SoundEvents.NOTE_BLOCK_PLING, 10, 10);
    }

    public static void Setting() {
        assert mc.player != null;
        assert mc.level != null;
        mc.player.playSound(SoundEvents.NOTE_BLOCK_PLING, 10, 10);
        if (settingCode == 0) {
            customMode = true;
            mc.player.sendMessage(Component.nullToEmpty("Enter the custom mode, you can set the size by yourself."), mc.player.getUUID());
            settingCode++;
        } else {
            longX = settingCode * 8;
            longZ = settingCode * 8;
            maxNumber = longX * longZ;
            mc.player.sendMessage(Component.nullToEmpty("The size has been set to : " + settingCode * 8 + "*" + settingCode * 8), mc.player.getUUID());
            settingCode += settingCode == 16 ? -16 : 1;
        }
    }

    public static void SetBlock(int x, int y, int z) {
        assert mc.player != null;
        assert mc.level != null;
        assert mc.gameMode != null;
        Vec3 v3d = new Vec3(x, y, z);
        BlockPos blockpos = new BlockPos(v3d);
        BlockHitResult blockRTR = new BlockHitResult(v3d, Direction.UP, blockpos, true);
        mc.gameMode.useItemOn(mc.player, mc.level, InteractionHand.MAIN_HAND, blockRTR);
    }

    public static void CrashRestart() {
        assert mc.player != null;
        if (!hadFile)
            mc.player.sendMessage(Component.nullToEmpty("This function only available in the map building mode."), mc.player.getUUID());
        else if (set) {
            assert mc.hitResult != null;
            BlockPos restartPoint = ((BlockHitResult) mc.hitResult).getBlockPos();
            int restart_x = restartPoint.getX();
            int restart_z = restartPoint.getZ();
            mc.player.sendMessage(Component.nullToEmpty("Set the restart point(The last block built last time) to {X:" + restart_x + "," + "Z:" + restart_z + "}."), mc.player.getUUID());
            mc.player.sendMessage(Component.nullToEmpty("After confirming that the other settings are correct,\nhit any block with feather to start building."), mc.player.getUUID());
            x = restart_x - base_x;
            z = restart_z - base_z;
            int restartNumber = (restart_x - base_x) + (restart_z - base_z) * longX;
            for (int i = 0; i < 128 * 128 - 1; i++) if (restartNumber == HAVE_BLOCK[i]) haveBlockI = i + 1;
        } else
            mc.player.sendMessage(Component.nullToEmpty("Set the reference point first!(Must be the same as last time)"), mc.player.getUUID());
    }
}