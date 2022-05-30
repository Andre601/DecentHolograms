package eu.decentsoftware.holograms.plugin.convertors;

import eu.decentsoftware.holograms.api.DecentHolograms;
import eu.decentsoftware.holograms.api.DecentHologramsAPI;
import eu.decentsoftware.holograms.api.convertor.ConvertorInfo;
import eu.decentsoftware.holograms.api.convertor.IConvertor;
import eu.decentsoftware.holograms.api.utils.Common;
import eu.decentsoftware.holograms.api.utils.config.Configuration;
import eu.decentsoftware.holograms.api.utils.location.LocationUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CMIConverter implements IConvertor {
    
    private static final DecentHolograms PLUGIN = DecentHologramsAPI.get();
    
    @Override
    public ConvertorInfo convert(CommandSender sender) {
        return convert(sender, new File("plugins/CMI/holograms.yml"));
    }
    
    @Override
    public ConvertorInfo convert(CommandSender sender, File file) {
        if(!ConverterCommon.isValidFile(file, "holograms.yml")){
            String fileName = file == null ? "UNKNOWN" : file.getName();
            
            Common.tell(sender, "%s&cInvalid file '%s' provided! Need 'holograms.yml' from CMI!", Common.PREFIX, fileName);
            return ConvertorInfo.failedConvert();
        }
        
        int converted = 0;
        int skipped = 0;
        int failed = 0;
        Configuration config = new Configuration(PLUGIN.getPlugin(), file);
        for(String name : config.getKeys(false)) {
            // Skip Auto-generated holograms to change pages.
            if(name.endsWith("#>") || name.endsWith("#<")) {
                Common.tell(sender, "%sSkipping auto-generated Hologram '%s'...", Common.PREFIX, name);
                ++skipped;
                continue;
            }
            
            Location loc = LocationUtils.asLocation(config.getString(name + ".Loc").replace(";", ":"));
            if(loc == null){
                Common.tell(sender, "%s&cHologram '%s' had an invalid location!", Common.PREFIX, name);
                ++failed;
                continue;
            }
            
            List<List<String>> pages = createPages(config.getStringList(name + ".Lines"));
            
            converted = ConverterCommon.createHologramPages(converted, name, loc, pages, PLUGIN);
        }
        
        return new ConvertorInfo(true, converted, skipped, failed);
    }
    
    @Override
    public ConvertorInfo convert(CommandSender sender, File... files) {
        int converted = 0;
        int skipped = 0;
        int failed = 0;
        for(final File file : files) {
            ConvertorInfo info = this.convert(sender, file);
            converted += info.getConverted();
            skipped += info.getSkipped();
            failed += info.getFailed();
        }
        return new ConvertorInfo(true, converted, skipped, failed);
    }
    
    @Override
    public List<String> prepareLines(List<String> lines){
        return lines.stream().map(line -> {
            if (line.toUpperCase(Locale.ROOT).startsWith("ICON:")) {
                return "#" + line;
            }
            return line;
        }).collect(Collectors.toList());
    }
    
    private List<List<String>> createPages(List<String> lines){
        List<String> temp = new ArrayList<>();
        List<List<String>> pages = new ArrayList<>();
        
        for (String line : lines) {
            if (line.toLowerCase(Locale.ROOT).equals("!nextpage!")) {
                pages.add(temp);
                temp.clear();
                continue;
            }
            
            temp.add(line);
        }
        
        if (!temp.isEmpty()) {
            pages.add(temp);
        }
        
        return pages.stream().map(this::prepareLines).collect(Collectors.toList());
    }
}
