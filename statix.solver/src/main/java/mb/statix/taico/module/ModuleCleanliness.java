package mb.statix.taico.module;

public enum ModuleCleanliness {
    /** Indicates that the module is guaranteed to be dirty. */
    DIRTY,
    
    /** Indicates that we are unsure if the module is dirty or not. */
    CLIRTY,
    
    /** Indicates that the module is guaranteed to be clean. */
    CLEAN
}
