package com.example.examplemod;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

@Mod(CCMod.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = CCMod.MODID)
public class CCMod {
    static final String MODID = "autobuild";
    private static final Minecraft mc = Minecraft.getInstance();
    private static boolean attackDown = false;
    private static boolean doingJob = false;
    private static Timer timer;
    private static boolean first = true;
    private static boolean set = false;
    private static BlockPos firstpoint = null;
    private static int base_x, base_y, base_z;
    private static int x = 0;
    private static int z = 0;
    private static int longX = 8;
    private static int longZ = 8;
    private static boolean positive = true;
    private static int number = 0;
    private static final int[] BLOCK = new int[128 * 128];
    private static final int[] HAVE_BLOCK = new int[128 * 128];
    private static boolean pause = false;
    private static int settingCode = 1;
    private static boolean hadFile = false;
    private static boolean cancelOrStop = false;
    private static boolean stop = false;
    private static boolean arrive = false;
    private static int haveBlockI = 0;
    private static int maxNumber = 64;

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {

        if (mc.options.keyAttack.isDown() && mc.player != null) {
            if (!attackDown) {

                if (mc.player.getMainHandItem().getItem() == Items.FEATHER) {
                    if (doingJob) {
                        if (stop) {
                            stop = false;
                            cancelOrStop = false;
                            mc.player.sendMessage(new StringTextComponent("Resume!"), mc.player.getUUID());
                        }
                    } else {
                        if (mc.hitResult != null && mc.hitResult.getType().equals(RayTraceResult.Type.BLOCK)) {
                            if (set) {
                                DoJob();
                                set = false;
                            } else {
                                SetPoint();
                            }
                        } else {
                            if (!hadFile)
                                LoadingMap();
                            else {
                                hadFile = false;
                                mc.player.sendMessage(new StringTextComponent("The map file is no longer used!"), mc.player.getUUID());
                            }

                        }
                    }
                }

                if (mc.player.getMainHandItem().getItem() == Items.STICK) {
                    if (doingJob) {
                        if (!cancelOrStop) {
                            stop = true;
                            cancelOrStop = true;
                            assert mc.level != null;
                            mc.level.playSound(mc.player, mc.player.getX(), mc.player.getY(), mc.player.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundCategory.VOICE, 10, 10);
                            mc.player.sendMessage(new StringTextComponent("Pause! Click with feather to resume,or click with stick again to cancel."), mc.player.getUUID());
                        } else {
                            stop = false;
                            cancelOrStop = false;
                            EndJob();
                            mc.player.sendMessage(new StringTextComponent("Cancel!"), mc.player.getUUID());
                        }
                    } else {
                        if (mc.hitResult != null && mc.hitResult.getType().equals(RayTraceResult.Type.BLOCK))
                            CrashRestart();
                        else
                            Setting();
                    }
                }

                attackDown = true;
            }
        } else {
            attackDown = false;
        }

    }

    public static void GOGO() {
        assert mc.player != null;

        timer.schedule(new TimerTask() {
            public void run() {
                if (mc.level == null)
                    timer.cancel();
                if (!stop) {
                    if (!hadFile)
                        ChangeBlock();
                    if (mc.player.getMainHandItem().getItem() == Items.AIR || mc.player.getMainHandItem().getItem() == Items.STICK || mc.player.getMainHandItem().getItem() == Items.FEATHER) {
                        if (!pause) {
                            mc.player.sendMessage(new StringTextComponent("Pause! Get some blocks in your hand to resume."), mc.player.getUUID());
                            pause = true;
                        }
                    } else {
                        pause = false;
                        if (!hadFile) {
                            if (doingJob)
                                SetAndRun();
                        } else {
                            mc.player.moveTo(base_x + 0.5 + x, base_y + 2, base_z + 0.5 + z);
                            WantGo();
                            if (arrive) {
                                ChooseAndSet();
                                arrive = false;
                                if (haveBlockI >= 128 * 128 - 1 || HAVE_BLOCK[haveBlockI] > maxNumber || HAVE_BLOCK[haveBlockI] == 0) {
                                    EndJob();
                                    mc.player.sendMessage(new StringTextComponent("Building completed according to the map!"), mc.player.getUUID());
                                }
                            }
                        }
                    }
                }
            }
        }, 0, 100);
    }

    public static void DoJob() {
        assert mc.player != null;
        assert mc.gameMode != null;
        doingJob = true;
        timer = new Timer();
        mc.player.sendMessage(new StringTextComponent("Start in 5s."), mc.player.getUUID());
        timer.schedule(new TimerTask() {
            public void run() {
                GOGO();
                assert mc.level != null;
                mc.level.playSound(mc.player, mc.player.getX(), mc.player.getY(), mc.player.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundCategory.VOICE, 10, 10);
            }
        }, 5000);
    }

    public static void SetAndRun() {
        assert mc.player != null;
        SetBlock(base_x + x, base_y + 1, base_z + z);
        mc.player.moveTo(base_x + 0.5 + x, base_y + 2, base_z + 0.5 + z);
        if (positive && x < longX) {
            x++;
        }
        if (!positive && x > -1) {
            x--;
        }
        if (positive && x == longX) {
            x--;
            if (z < longZ - 1) {
                z++;
            } else {
                EndJob();
                mc.player.sendMessage(new StringTextComponent("Done!"), mc.player.getUUID());
            }
            positive = false;
        }
        if (!positive && x == -1) {
            x++;
            if (z < longZ - 1) {
                z++;
            } else {
                EndJob();
                mc.player.sendMessage(new StringTextComponent("Done!"), mc.player.getUUID());
            }
            positive = true;
        }
    }

    public static void WantGo() {
        assert mc.player != null;
        number = HAVE_BLOCK[haveBlockI];
        int want_z = number / longX;
        int want_x = number % longX;
        if (x == want_x && z == want_z) {
            arrive = true;
            haveBlockI++;
        } else {
            if (want_x < x)
                if (x - want_x >= 4)
                    x -= 4;
                else
                    x--;
            else if (want_x > x)
                if (want_x - x >= 4)
                    x += 4;
                else
                    x++;
            if (want_z > z)
                if (want_z - z >= 4)
                    z += 4;
                else
                    z++;
            else if (want_z < z)
                if (z - want_z >= 4)
                    z -= 4;
                else
                    z--;
        }
    }

    public static void ChooseAndSet() {
        assert mc.player != null;
        int BlockNumber = BLOCK[number] - 48;
        if (BlockNumber > -1 && BlockNumber < 9)
            mc.player.inventory.selected = BlockNumber;
        SetBlock(base_x + x, base_y + 1, base_z + z);
    }

    public static void ChangeBlock() {
        if (pause) {
            assert mc.player != null;
            if (mc.player.getMainHandItem().getItem() == Items.AIR)
                if (mc.player.inventory.selected < 8) {
                    mc.player.inventory.selected++;
                } else {
                    mc.player.inventory.selected = 0;
                    assert mc.level != null;
                    mc.level.playSound(mc.player, mc.player.getX(), mc.player.getY(), mc.player.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundCategory.VOICE, 10, 10);
                    mc.player.sendMessage(new StringTextComponent("No Block in bar!"), mc.player.getUUID());
                }
        }
    }

    public static void SetPoint() {
        assert mc.hitResult != null;
        if (first) {
            set = false;
            firstpoint = ((BlockRayTraceResult) mc.hitResult).getBlockPos();
            assert mc.player != null;
            mc.player.sendMessage(new StringTextComponent("Set the reference point to : " + firstpoint + " ?"), mc.player.getUUID());
            mc.player.sendMessage(new StringTextComponent("Do it again to confirm the reference point."), mc.player.getUUID());
            first = false;
        } else if (((BlockRayTraceResult) mc.hitResult).getBlockPos().equals(firstpoint)) {
            base_x = firstpoint.getX();
            base_y = firstpoint.getY();
            base_z = firstpoint.getZ();
            assert mc.player != null;
            mc.player.sendMessage(new StringTextComponent("Reference point : " + firstpoint + " ." + "Put your feather and stick in the first two bar,then hit any block with feather to start building."), mc.player.getUUID());
            set = true;
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
        if (!HadDir) {
            mc.player.sendMessage(new StringTextComponent("A new folder named 'CCMod' has been created in the game folder."), mc.player.getUUID());
        }
        try {
            hadFile = !file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (hadFile) {
            mc.player.sendMessage(new StringTextComponent("The map file is loaded. Click with stick to set the target size."), mc.player.getUUID());
            Reader reader;
            try {
                reader = new InputStreamReader(new FileInputStream(file));
                int tempChar;
                int i = 0;
                int j = 0;
                while ((tempChar = reader.read()) != -1) {
                    if (((char) tempChar) != '\n' && ((char) tempChar) != '\r') {
                        BLOCK[i] = (char) tempChar;
                        if (BLOCK[i] - 48 > -1 && BLOCK[i] - 48 < 9) {
                            HAVE_BLOCK[j] = i;
                            j++;
                        }
                        i++;
                    }
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mc.player.sendMessage(new StringTextComponent("A new folder named 'file.txt' has been created in 'CCMod'."), mc.player.getUUID());
        }
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
        mc.level.playSound(mc.player, mc.player.getX(), mc.player.getY(), mc.player.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundCategory.VOICE, 10, 10);
    }


    public static void Setting() {
        assert mc.player != null;
        assert mc.level != null;
        mc.level.playSound(mc.player, mc.player.getX(), mc.player.getY(), mc.player.getZ(), SoundEvents.NOTE_BLOCK_PLING, SoundCategory.VOICE, 10, 10);
        if (settingCode > 16) {
            settingCode = 1;
            Setting();
        } else {
            longX = settingCode * 8;
            longZ = settingCode * 8;
            maxNumber = longX * longZ;
            mc.player.sendMessage(new StringTextComponent("The size has been set to : " + settingCode * 8 + "*" + settingCode * 8), mc.player.getUUID());
            settingCode++;
        }
    }


    public static void SetBlock(int x, int y, int z) {
        assert mc.player != null;
        assert mc.level != null;
        assert mc.gameMode != null;
        Vector3d v3d = new Vector3d(x, y, z);
        BlockPos blockpos = new BlockPos(v3d);
        BlockRayTraceResult blockRTR = new BlockRayTraceResult(v3d, Direction.UP, blockpos, true);
        mc.gameMode.useItemOn(mc.player, mc.level, Hand.MAIN_HAND, blockRTR);
    }

    public static void CrashRestart() {
        assert mc.player != null;
        if (!hadFile) {

            mc.player.sendMessage(new StringTextComponent("This function only available in the map building mode."), mc.player.getUUID());
        }
        else if (set) {
            assert mc.hitResult != null;
            BlockPos restartPoint = ((BlockRayTraceResult) mc.hitResult).getBlockPos();
            int restart_x = restartPoint.getX();
            int restart_z = restartPoint.getZ();
            mc.player.sendMessage(new StringTextComponent("Set the restart point(The last block built last time) to {X:" + restart_x + "," + "Z:" + restart_z + "}."), mc.player.getUUID());
            mc.player.sendMessage(new StringTextComponent("After confirming that the other settings are correct, hit any block with feather to start building."), mc.player.getUUID());
            x = restart_x - base_x;
            z = restart_z - base_z;
            int restartNumber = (restart_x - base_x) + (restart_z - base_z) * longX;
            for (int i = 0; i < 128 * 128 - 1; i++) {
                if (restartNumber == HAVE_BLOCK[i]) {
                    haveBlockI = i + 1;
                }
            }
        } else
            mc.player.sendMessage(new StringTextComponent("Set the reference point(Must be the same as last time) first!"), mc.player.getUUID());
    }

}