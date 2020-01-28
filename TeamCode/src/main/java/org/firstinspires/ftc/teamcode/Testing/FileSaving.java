package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.virus.agobot.Agobot;

import java.io.File;
//@TeleOp
public class FileSaving extends LinearOpMode {
    File test = AppUtil.getInstance().getSettingsFile("test.txt");

    @Override
    public void runOpMode() throws InterruptedException {
        waitForStart();
        ReadWriteFile.writeFile(test, "it works");
        File test2 = AppUtil.getInstance().getSettingsFile("test.txt");
        String textData = ReadWriteFile.readFile(test2).trim();
        while (opModeIsActive()){
            telemetry.addData("Text",textData);
            telemetry.update();
        }
    }
}
