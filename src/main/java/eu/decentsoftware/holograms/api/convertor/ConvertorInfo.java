package eu.decentsoftware.holograms.api.convertor;

public class ConvertorInfo{
    
    private final boolean successful;
    private final int converted;
    private final int skipped;
    private final int failed;
    
    public ConvertorInfo(boolean successful, int converted, int skipped, int failed) {
        this.successful = successful;
        this.converted = converted;
        this.skipped = skipped;
        this.failed = failed;
    }
    
    public static ConvertorInfo failedConvert() {
        return new ConvertorInfo(false, 0, 0, 0);
    }
    
    public boolean isSuccessful() {
        return successful;
    }
    
    public int getConverted() {
        return converted;
    }
    
    public int getSkipped() {
        return skipped;
    }
    
    public int getFailed() {
        return failed;
    }
    
    public int getTotal() {
        return converted + skipped + failed;
    }
}
