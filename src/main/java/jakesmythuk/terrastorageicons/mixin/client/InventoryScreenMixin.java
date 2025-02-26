package jakesmythuk.terrastorageicons.mixin.client;

import jakesmythuk.terrastorageicons.client.IButton;
import me.timvinci.terrastorage.client.gui.widget.StorageButtonWidget;
import me.timvinci.terrastorage.network.ClientNetworkHandler;
import me.timvinci.terrastorage.util.client.ButtonsStyle;
import me.timvinci.terrastorage.util.StorageAction;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = InventoryScreen.class, priority = 1001)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> {
    @Shadow @Final private RecipeBookComponent recipeBookComponent;
    @Shadow private boolean buttonClicked;
    @Unique
    private StorageButtonWidget sortInventoryButton;
    @Unique
    private StorageButtonWidget quickStackButton;

    public InventoryScreenMixin(InventoryMenu screenHandler, Inventory playerInventory, Component text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(
            method = {"init"},
            at = {@At("TAIL")},
            cancellable = true
    )
    public void onInit(CallbackInfo ci) {
        if (!this.minecraft.player.isSpectator()) {
            int buttonX = this.leftPos + 128;
            int buttonY = this.height / 2 - 22;
            this.quickStackButton = new StorageButtonWidget(
                    buttonX, buttonY, 16, 16,
                    Component.empty(), ButtonsStyle.DEFAULT, (onPress) -> {
                ClientNetworkHandler.sendActionPayload(StorageAction.QUICK_STACK_TO_NEARBY);
            });
            this.quickStackButton.setTooltip(Tooltip.create(Component.translatable("terrastorage.button.tooltip.quick_stack_to_nearby")));
            IButton quickStackIBtn = (IButton) this.quickStackButton;
            quickStackIBtn.setIconCoords(48, 16);
            quickStackIBtn.canBeTextified(false);
            this.addRenderableWidget(this.quickStackButton);
            buttonX += 20;
            this.sortInventoryButton = new StorageButtonWidget(
                    buttonX, buttonY, 16, 16,
                    Component.empty(), ButtonsStyle.DEFAULT, (onPress) -> {
                ClientNetworkHandler.sendSortPayload(true);
            });
            this.sortInventoryButton.setTooltip(Tooltip.create(Component.translatable("terrastorage.button.tooltip.sort_inventory")));
            IButton sortInventoryIBtn = (IButton) this.sortInventoryButton;
            sortInventoryIBtn.setIconCoords(0, 16);
            sortInventoryIBtn.canBeTextified(false);
            this.addRenderableWidget(this.sortInventoryButton);
        }
        ci.cancel();
    }

    @Redirect(
            method = {"init"},
            at = @At(
                    value = "NEW",
                    target = "(IIIILnet/minecraft/client/gui/components/WidgetSprites;Lnet/minecraft/client/gui/components/Button$OnPress;)Lnet/minecraft/client/gui/components/ImageButton;"
            )
    )
    private ImageButton modifyRecipeBookButtonPress(int x, int y, int width, int height, WidgetSprites textures, Button.OnPress pressAction) {
        return new ImageButton(x, y, width, height, textures, modifiedRecipeBookButtonPress((button) -> {
            this.recipeBookComponent.toggleVisibility();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
            button.setPosition(this.leftPos + 104, this.height / 2 - 22);
            this.buttonClicked = true;
        }));
    }
    @Unique
    private Button.OnPress modifiedRecipeBookButtonPress(Button.OnPress original) {
        return this.minecraft.player.isSpectator() ? original : (button) -> {
            original.onPress(button);
            int buttonX = this.leftPos + 128;
            this.quickStackButton.setPosition(buttonX, this.quickStackButton.getY());
            buttonX += 24;
            this.sortInventoryButton.setPosition(buttonX, this.sortInventoryButton.getY());
        };
    }
}