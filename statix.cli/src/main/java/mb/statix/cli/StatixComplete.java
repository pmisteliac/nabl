package mb.statix.cli;

import org.apache.commons.vfs2.FileObject;
import org.metaborg.core.MetaborgException;
import org.metaborg.spoofax.core.Spoofax;
import org.metaborg.util.log.ILogger;
import org.metaborg.util.log.LoggerUtils;
public class StatixComplete {

    private static final ILogger log = LoggerUtils.logger(StatixComplete.class);

    private final Statix STX;
    private final Spoofax spoofax;

    public StatixComplete(Statix stx, Spoofax spoofax) {
        this.STX = stx;
        this.spoofax = spoofax;
    }

    public void run(String specFile, String inputFile) throws MetaborgException, InterruptedException {
        final FileObject specResource = this.spoofax.resolve(specFile);

    	System.out.println(specFile);
        System.out.println(inputFile);
        
        log.info("Completing file {} based on spec from file {}", inputFile, specFile);
    }


}