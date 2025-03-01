package ballistix.api.radar;

import net.minecraft.item.Item;
import net.minecraft.util.math.vector.Vector3d;

public interface IDetected {

    Vector3d getPosition();

    Item getItem();

    boolean showBearing();

    public static class Detected implements IDetected {
    	
    	private final Vector3d position;
    	private final Item item;
    	private boolean showBearing;
    	
    	public Detected(Vector3d position, Item item, boolean showBearing) {
    		this.position = position;
    		this.item = item;
    		this.showBearing = showBearing;
    	}

        @Override
        public Vector3d getPosition() {
            return position;
        }

        @Override
        public Item getItem() {
            return item;
        }

        @Override
        public boolean showBearing() {
            return showBearing;
        }
    }

}
