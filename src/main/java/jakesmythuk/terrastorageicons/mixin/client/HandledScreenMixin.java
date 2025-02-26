package jakesmythuk.terrastorageicons.mixin.client;

import jakesmythuk.terrastorageicons.client.IScreen;
import jakesmythuk.terrastorageicons.TerrastorageIconsClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractContainerScreen.class, priority = 2000)
public abstract class HandledScreenMixin<T extends AbstractContainerMenu> extends Screen implements MenuAccess<T>, IScreen {
	@Shadow
	protected int imageWidth;
	@Shadow
	protected int imageHeight;
	@Shadow
	protected int leftPos;
	@Shadow
	protected int topPos;

	protected HandledScreenMixin(Component title) {
		super(title);
	}

	@Inject(
			method = "init",
			at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;topPos:I", shift = At.Shift.AFTER),
			cancellable = true
	)
	private void onInit(CallbackInfo ci) {
		TerrastorageIconsClient.initScreen((AbstractContainerScreen<?>)(Object)this, this);
		ci.cancel();
	}

	@Override
	public int backgroundHeight() {
		return imageHeight;
	}

	@Override
	public int y() {
		return topPos;
	}

	@Override
	public int backgroundWidth() {
		return imageWidth;
	}

	@Override
	public int x() {
		return leftPos;
	}

	@Override
	public Font textRenderer() {
		return font;
	}

	@Override
	public <T extends GuiEventListener & Renderable & NarratableEntry> T terrastorageIcons$addDrawableChild(T button) {
		return addRenderableWidget(button);
	}

	@Override
	public Minecraft client() {
		return minecraft;
	}
}