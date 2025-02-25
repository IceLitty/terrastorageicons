package jakesmythuk.terrastorageicons.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import jakesmythuk.terrastorageicons.IButton;
import jakesmythuk.terrastorageicons.TerrastorageIconsClient;
import me.timvinci.terrastorage.config.ClientConfigManager;
import me.timvinci.terrastorage.gui.widget.StorageButtonWidget;
import me.timvinci.terrastorage.util.ButtonsStyle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static jakesmythuk.terrastorageicons.TerrastorageIcons.*;

@Mixin(StorageButtonWidget.class)
public abstract class StorageButtonWidgetMixin extends ButtonWidget implements IButton {
    private int iconOffsetX = 0, iconOffsetY = 0;
    private float selectedFrame = 0;
    private boolean canBeTextified = true;
    protected StorageButtonWidgetMixin(int x, int y, int width, int height, Text message, PressAction onPress, NarrationSupplier narrationSupplier) {
        super(x, y,
                ClientConfigManager.getInstance().getConfig().getButtonsStyle() == ButtonsStyle.TEXT_ONLY ? width : 16,
                ClientConfigManager.getInstance().getConfig().getButtonsStyle() == ButtonsStyle.TEXT_ONLY ? height : 16,
                message, onPress, narrationSupplier);
    }

    @Inject(at = @At("HEAD"), method = "renderWidget", cancellable = true)
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        context.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (canBeTextified && ClientConfigManager.getInstance().getConfig().getButtonsStyle() == ButtonsStyle.TEXT_ONLY){
            int i = this.hovered ? 16776960 : 16777215;

            this.drawMessage(context, minecraftClient.textRenderer, i | MathHelper.ceil(this.alpha * 255.0F) << 24);
        } else {
            TerrastorageIconsClient.renderButton(this, context, delta);
        }
        context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        ci.cancel();
    }

    public StorageButtonWidget setIconCoords(int x, int y) {
        this.iconOffsetX = x;
        this.iconOffsetY = y;
        return (StorageButtonWidget)(Object)this;
    }

    public int getIconY() {
        int i = 1;
        if (!this.active)
            i = 0;
        else if (this.isSelected())
            i = 2 + (int) selectedFrame;
        return iconOffsetY + i * 16;
    }

    public float selectedFrame() {
        return selectedFrame;
    }

    public void selectedFrame(float selectedFrame) {
        this.selectedFrame = selectedFrame;
    }

    public int iconOffsetX() {
        return iconOffsetX;
    }

    public void iconOffsetX(int iconOffsetX) {
        this.iconOffsetX = iconOffsetX;
    }

    public int iconOffsetY() {
        return iconOffsetY;
    }

    public void iconOffsetY(int iconOffsetY) {
        this.iconOffsetY = iconOffsetY;
    }

    public void canBeTextified(boolean canBeTextified) {
        this.canBeTextified = canBeTextified;
    }
}
