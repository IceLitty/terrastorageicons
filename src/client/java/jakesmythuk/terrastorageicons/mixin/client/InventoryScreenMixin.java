package jakesmythuk.terrastorageicons.mixin.client;

import jakesmythuk.terrastorageicons.IButton;
import me.timvinci.terrastorage.gui.widget.StorageButtonWidget;
import me.timvinci.terrastorage.network.ClientNetworkHandler;
import me.timvinci.terrastorage.util.StorageAction;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InventoryScreen.class, priority = 1001)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> {
    @Shadow @Final private RecipeBookWidget recipeBook;
    @Shadow private boolean mouseDown;
    @Unique
    private StorageButtonWidget sortInventoryButton;
    @Unique
    private StorageButtonWidget quickStackButton;

    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(
            method = {"init"},
            at = {@At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/InventoryScreen;setInitialFocus(Lnet/minecraft/client/gui/Element;)V", shift = At.Shift.AFTER)},
            cancellable = true
    )
    public void onInit(CallbackInfo ci) {
        if (!this.client.player.isSpectator()) {
            int buttonX = this.x + 128;
            int buttonY = this.height / 2 - 22;
            this.quickStackButton = new StorageButtonWidget(
                    buttonX, buttonY, 16, 16,
                    Text.empty(), Tooltip.of(Text.translatable("terrastorage.button.tooltip.quick_stack_to_nearby")), (onPress) -> {
                ClientNetworkHandler.sendActionPacket(StorageAction.QUICK_STACK_TO_NEARBY);
            });
            IButton quickStackIBtn = (IButton) this.quickStackButton;
            quickStackIBtn.setIconCoords(48, 16);
            quickStackIBtn.canBeTextified(false);
            this.addDrawableChild(this.quickStackButton);
            buttonX += 20;
            this.sortInventoryButton = new StorageButtonWidget(
                    buttonX, buttonY, 16, 16,
                    Text.empty(), Tooltip.of(Text.translatable("terrastorage.button.tooltip.sort_inventory")), (onPress) -> {
                ClientNetworkHandler.sendPlayerSortPacket();
            });
            IButton sortInventoryIBtn = (IButton) this.sortInventoryButton;
            sortInventoryIBtn.setIconCoords(0, 16);
            sortInventoryIBtn.canBeTextified(false);
            this.addDrawableChild(this.sortInventoryButton);
        }
        ci.cancel();
    }

    @Redirect(
            method = {"init"},
            at = @At(
                    value = "NEW",
                    target = "(IIIIIIILnet/minecraft/util/Identifier;Lnet/minecraft/client/gui/widget/ButtonWidget$PressAction;)Lnet/minecraft/client/gui/widget/TexturedButtonWidget;"
            )
    )
    private TexturedButtonWidget modifyRecipeBookButtonPress(int x, int y, int width, int height, int u, int v, int hoveredVOffset, Identifier texture, ButtonWidget.PressAction pressAction) {
        return new TexturedButtonWidget(x, y, width, height, u, v, hoveredVOffset, texture, modifiedRecipeBookButtonPress((button) -> {
            this.recipeBook.toggleOpen();
            this.x = this.recipeBook.findLeftEdge(this.width, this.backgroundWidth);
            button.setPosition(this.x + 104, this.height / 2 - 22);
            this.mouseDown = true;
        }));
    }
    @Unique
    private ButtonWidget.PressAction modifiedRecipeBookButtonPress(ButtonWidget.PressAction original) {
        return this.client.player.isSpectator() ? original : (button) -> {
            original.onPress(button);
            int buttonX = this.x + 128;
            this.quickStackButton.setPosition(buttonX, this.quickStackButton.getY());
            buttonX += 24;
            this.sortInventoryButton.setPosition(buttonX, this.sortInventoryButton.getY());
        };
    }
}