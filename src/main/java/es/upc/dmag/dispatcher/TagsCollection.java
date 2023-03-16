package es.upc.dmag.dispatcher;

import java.util.Arrays;
import java.util.Iterator;

public class TagsCollection implements Iterable<AbstractTag>{

    private final AbstractTag[] tags;

    public TagsCollection(AbstractTag[] tags) {
        Arrays.sort(tags);
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TagsCollection)) return false;
        TagsCollection that = (TagsCollection) o;
        if(tags.length != ((TagsCollection) o).tags.length){
            return false;
        }
        for(int tag_i=0; tag_i < tags.length; tag_i++){
            if(!tags[tag_i].equals(((TagsCollection) o).tags[tag_i])){
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(tags);
    }

    @Override
    public Iterator<AbstractTag> iterator() {
        return Arrays.asList(tags).iterator();
    }
}
