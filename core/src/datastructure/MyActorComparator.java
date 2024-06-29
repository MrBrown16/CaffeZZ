package datastructure;

import java.util.Comparator;

import com.mygdx.game.MyActor;

public class MyActorComparator implements Comparator<MyActor> {

    @Override
    public int compare(MyActor o1, MyActor o2) {
        return Float.compare(o1.getIsometricDepth() , o2.getIsometricDepth() );  
    }
    
}
