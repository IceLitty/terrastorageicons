package jakesmythuk.terrastorageicons.mixin.client;

import me.timvinci.terrastorage.gui.widget.StorageButtonCreator;
import me.timvinci.terrastorage.gui.widget.StorageButtonWidget;
import me.timvinci.terrastorage.util.ButtonsStyle;
import me.timvinci.terrastorage.util.StorageAction;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StorageButtonCreator.class)
public class StorageButtonCreatorMixin {
	@Inject(at = @At("HEAD"), method = "createStorageButton")
	private static void init(StorageAction action, int x, int y, int width, int height, Text buttonText, ButtonsStyle buttonStyle, CallbackInfoReturnable<StorageButtonWidget> cir) {

	}
}