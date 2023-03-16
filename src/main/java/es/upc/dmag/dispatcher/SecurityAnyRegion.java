package es.upc.dmag.dispatcher;

import htsjdk.samtools.SAMRecord;

public class SecurityAnyRegion extends SecurityRegion{
    public SecurityAnyRegion() {
        super(0, 0, 0);
    }

    public boolean intersects(SAMRecord samRecord){
        return true;
    }
}
