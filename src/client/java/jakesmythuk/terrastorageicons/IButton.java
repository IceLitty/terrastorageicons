package jakesmythuk.terrastorageicons;

import me.timvinci.terrastorage.gui.widget.StorageButtonWidget;

public interface IButton {
    StorageButtonWidget setIconCoords(int x, int y);
    int getIconY();
    float selectedFrame();
    void selectedFrame(float selectedFrame);
    int iconOffsetX() ;
    void iconOffsetX(int iconOffsetX) ;
    int iconOffsetY();
    void iconOffsetY(int iconOffsetY);
    void canBeTextified(boolean canBeTextified);

    int getX();
    int getY();
    int getWidth();
    int getHeight();
}
