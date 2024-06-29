package datastructure;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;

import com.mygdx.game.FurnitureActor;
import com.mygdx.game.MyActor;


public class ReorderableArrayList<T> extends ArrayList<T> {
    private static final long serialVersionUID = 1L;
	private static Field elementDataField;

	static {
		try {
			// Get the 'elementData' field from the ArrayList class
			elementDataField = ArrayList.class.getDeclaredField("elementData");
			elementDataField.setAccessible(true); // Make it accessible
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}

    @SuppressWarnings("unchecked")
    private T[] getElementData() {
        try {
            return (T[]) elementDataField.get(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access elementData field", e);
        }
    }

    public void moveElement(int originalIndex, int newIndex) {
        if (originalIndex < 0 || originalIndex >= size() || newIndex < 0 || newIndex >= size()) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }

        if (originalIndex == newIndex) {
            return; // No need to move if the indices are the same
        }

        try {
            T[] array = getElementData();

            // Store the element to be moved
            T element = array[originalIndex];

            if (originalIndex < newIndex) {
                // Shift elements to the left
                System.arraycopy(array, originalIndex + 1, array, originalIndex, newIndex - originalIndex);
            } else {
                // Shift elements to the right
                System.arraycopy(array, newIndex, array, newIndex + 1, originalIndex - newIndex);
            }

            // Place the element at the new position
            array[newIndex] = element;

        } catch (RuntimeException e) {
            System.err.println("Reflection failed: " + e.getMessage());
            safeMoveElement(this, originalIndex, newIndex);
        }
    }
    public void safeMoveElement(ArrayList<T> list, int originalIndex, int newIndex) {
        T element = list.remove(originalIndex);
        list.add(newIndex, element);
    }

    public void moveToSortedPosition(int currentIndex, Comparator<T> comparator) {
        if (currentIndex < 0 || currentIndex >= size()) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }

        T currentElement = get(currentIndex);

        // Find new position
        int newIndex = currentIndex;
        while (newIndex > 0 && comparator.compare(currentElement, get(newIndex - 1)) < 0) {
            newIndex--;
        }
        while (newIndex < size() - 1 && comparator.compare(currentElement, get(newIndex + 1)) > 0) {
            newIndex++;
        }
        
        if (newIndex != currentIndex) {
            moveElement(currentIndex, newIndex);
        }
    }
    @SuppressWarnings("unchecked")
    public void moveToSortedPosition(T currentElement) {
        if (currentElement.getClass() == Float.class) {
            Comparator<Float> comparator = Float::compare;

            moveToSortedPosition(this.indexOf(currentElement), (Comparator<T>)comparator);
        }
        if (currentElement.getClass() == MyActor.class) {
            MyActorComparator comparator = new MyActorComparator();

            moveToSortedPosition(this.indexOf(currentElement), (Comparator<T>)comparator);
        }
    }
    
	
}
