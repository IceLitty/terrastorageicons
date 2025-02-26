package jakesmythuk.terrastorageicons.mixin.client;

import me.timvinci.terrastorage.client.gui.widget.StorageButtonCreator;
import me.timvinci.terrastorage.client.gui.widget.StorageButtonWidget;
import me.timvinci.terrastorage.util.client.ButtonsStyle;
import me.timvinci.terrastorage.util.StorageAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StorageButtonCreator.class)
public class StorageButtonCreatorMixin {
	@Inject(at = @At("HEAD"), method = "createStorageButton")
	private static void init(StorageAction action, int x, int y, int width, int height, Component buttonText, ButtonsStyle buttonStyle, CallbackInfoReturnable<StorageButtonWidget> cir) {

	}
}