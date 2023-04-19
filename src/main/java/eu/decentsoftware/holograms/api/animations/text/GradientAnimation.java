package eu.decentsoftware.holograms.api.animations.text;

import eu.decentsoftware.holograms.api.animations.TextAnimation;
import eu.decentsoftware.holograms.api.utils.Common;
import eu.decentsoftware.holograms.api.utils.color.IridiumColorAPI;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;

import java.awt.Color;

public class GradientAnimation extends TextAnimation{
    protected GradientAnimation(){
        super("gradient", 3, 0);
    }
    
    @Override
    public String animate(@NonNull String string, long step, String... args){
        //<#ANIM:gradient:start:end:steps>
        // Require at least start and end.
        if (args.length < 2) {
            return string;
        }
        
        // Only allow hex colours
        if (args[0].length() < 7 || args[1].length() < 7 || !args[0].startsWith("#") || !args[1].startsWith("#")) {
            return string;
        }
        
        Color start;
        Color end;
        try {
            start = new Color(Integer.parseInt(args[0].substring(1), 16));
            end = new Color(Integer.parseInt(args[1].substring(1), 16));
        }catch (NumberFormatException ex) {
            return string;
        }
        
        int steps = -1;
        if (args.length >= 3 && !args[2].isEmpty()){
            steps = Common.parseInt(args[2]);
        }
        
        if (steps <= -1) {
            steps = 10;
        }
        
        ChatColor[] colorSteps = IridiumColorAPI.createGradient(start, end, steps);
        
        int currentStep = getCurrentStep(step, steps);
        
        return colorSteps[currentStep] + string;
    }
}
