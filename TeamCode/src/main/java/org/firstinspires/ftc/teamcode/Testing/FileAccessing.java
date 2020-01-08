package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

import java.io.File;
@TeleOp
public class FileAccessing extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        File test = AppUtil.getInstance().getSettingsFile("test.txt");
        String textData = ReadWriteFile.readFile(test).trim();
        waitForStart();
        while (opModeIsActive()){
            telemetry.addData("Text",textData);
            telemetry.update();
        }
    }
}
