package ballistix.api.radar;

import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;

public interface IDetected {

    Vec3 getPosition();

    Item getItem();

    boolean showBearing();

    public static record Detected(Vec3 position, Item item, boolean showBearing) implements IDetected {

        @Override
        public Vec3 getPosition() {
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
