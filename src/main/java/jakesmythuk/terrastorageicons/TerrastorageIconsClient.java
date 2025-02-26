package jakesmythuk.terrastorageicons;

import jakesmythuk.terrastorageicons.client.IButton;
import jakesmythuk.terrastorageicons.client.IScreen;
import me.timvinci.terrastorage.config.client.ClientConfigManager;
import me.timvinci.terrastorage.client.gui.TerrastorageOptionsScreen;
import me.timvinci.terrastorage.client.gui.widget.StorageButtonCreator;
import me.timvinci.terrastorage.client.gui.widget.StorageButtonWidget;
import me.timvinci.terrastorage.util.client.ButtonsPlacement;
import me.timvinci.terrastorage.util.client.ButtonsStyle;
import me.timvinci.terrastorage.util.client.LocalizedTextProvider;
import me.timvinci.terrastorage.util.StorageAction;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static jakesmythuk.terrastorageicons.TerrastorageIcons.*;

public class TerrastorageIconsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

	}

	public static void renderButton(IButton button, GuiGraphics context, float delta) {
		StorageButtonWidget widget = ((StorageButtonWidget)(Object)button);
		if (widget.isHoveredOrFocused()) {
			button.selectedFrame(button.selectedFrame() + delta * 0.4f);
			if (button.selectedFrame() >= 3)
				button.selectedFrame(2);
		} else
			button.selectedFrame(0);

		if (ClientConfigManager.getInstance().getConfig().getButtonsStyle() == ButtonsStyle.DEFAULT) {
			context.blit(ICONS_TEXTURE,
					button.getX(), button.getY(),
					16, 16,
					button.iconOffsetX(), button.iconOffsetY() + (button.getIconY() - button.iconOffsetY()) * 2,
					16, 16,
					TEXTURE_WIDTH, TEXTURE_HEIGHT);
		}
		context.setColor(1.0F, 1.0F, 1.0F, 1.0F);
	}


	public static void initScreen(AbstractContainerScreen<?> widget, IScreen screen) {
		if (!Minecraft.getInstance().player.isSpectator()) {
			if (widget.getMenu().slots.size() - 36 >= 27) {
				boolean isEnderChest = false;
				if (widget.getMenu() instanceof ChestMenu && widget.getTitle().equals(Component.translatable("container.enderchest"))) {
					isEnderChest = true;
				}

				StorageAction[] buttonActions = StorageAction.getButtonsActions(isEnderChest);
				int buttonWidth = 70;
				int buttonHeight = 15;
				int buttonSpacing = 2;
				int buttonPadding = 5;
				if (ClientConfigManager.getInstance().getConfig().getButtonsStyle() == ButtonsStyle.DEFAULT) {
					buttonWidth = 16;
					buttonHeight = 16;
					buttonSpacing = 4;
					buttonPadding = 4;
				}
				int buttonX = ClientConfigManager.getInstance().getConfig().getButtonsPlacement() == ButtonsPlacement.RIGHT ? screen.x() + screen.backgroundWidth() + buttonPadding : screen.x() - (buttonWidth + buttonPadding);
				int containerHeight = screen.backgroundHeight() - 94;
				int buttonSectionHeight = buttonActions.length * buttonHeight + (buttonActions.length - 1) * buttonSpacing;
				int buttonY = screen.y();
				StorageAction[] _buttonActions;
				int optionsButtonY;
				int i;
				StorageAction storageAction;
				Component buttonText;
				Tooltip buttonTooltip;
				StorageButtonWidget button;
				if (ClientConfigManager.getInstance().getConfig().getButtonsStyle() == ButtonsStyle.TEXT_ONLY) {
					_buttonActions = buttonActions;
					optionsButtonY = buttonActions.length;

					for(i = 0; i < optionsButtonY; ++i) {
						storageAction = _buttonActions[i];
						buttonText = (Component) LocalizedTextProvider.buttonTextCache.get(storageAction);
						buttonWidth = screen.textRenderer().width(buttonText) + 6;
						buttonTooltip = (Tooltip)LocalizedTextProvider.buttonTooltipCache.get(storageAction);
						button = StorageButtonCreator.createStorageButton(storageAction, buttonX, buttonY, buttonWidth, buttonHeight, buttonText, ButtonsStyle.DEFAULT);
						button.setTooltip(buttonTooltip);
						screen.terrastorageIcons$addDrawableChild(button);
						buttonY += buttonHeight + buttonSpacing;
					}

					if (ClientConfigManager.getInstance().getConfig().getDisplayOptionsButton()) {
						int optionsButtonX = (screen.client().screen.width - 120) / 2;
						optionsButtonY = screen.y() - 20;
						Button optionsButtonWidget = getButtonWidget(optionsButtonX, optionsButtonY, 120, 15,
								Component.translatable("terrastorage.button.options"),
								Tooltip.create(Component.translatable("terrastorage.button.tooltip.options")), clickOptions(screen), 32, 16);
						//optionsButtonWidget.setTooltip(Tooltip.create(Component.translatable("terrastorage.button.tooltip.options")));
						screen.terrastorageIcons$addDrawableChild(optionsButtonWidget);
					}
				} else {
					_buttonActions = buttonActions;
					optionsButtonY = buttonActions.length;


					if (ClientConfigManager.getInstance().getConfig().getDisplayOptionsButton()) {
						//optionsButtonY = screen.y() - 20;
						StorageButtonWidget storageButtonWidget = new StorageButtonWidget(buttonX, buttonY, buttonWidth, buttonHeight,
								Component.translatable("terrastorage.button.options"),
								ButtonsStyle.DEFAULT, clickOptions(screen));
						storageButtonWidget.setTooltip(Tooltip.create(Component.translatable("terrastorage.button.tooltip.options")));
						Button optionsButtonWidget = editButtonWidget(storageButtonWidget, 32, 16);
						screen.terrastorageIcons$addDrawableChild(optionsButtonWidget);
						buttonY += buttonHeight + buttonSpacing;
					}

					int posX = 0, posY = 0;
					for(i = 0; i < optionsButtonY; ++i) {
						storageAction = _buttonActions[i];
						buttonText = LocalizedTextProvider.buttonTextCache.get(storageAction);
						buttonTooltip = LocalizedTextProvider.buttonTooltipCache.get(storageAction);
						StorageButtonWidget storageButtonWidget = StorageButtonCreator.createStorageButton(storageAction, buttonX, buttonY, buttonWidth, buttonHeight, buttonText, ButtonsStyle.DEFAULT);
						storageButtonWidget.setTooltip(buttonTooltip);
						button = editButtonWidget(
								storageButtonWidget,
								posX, posY
						);
						screen.terrastorageIcons$addDrawableChild(button);
						buttonY += buttonHeight + buttonSpacing;
						posX += 16;
						if (posX >= TerrastorageIcons.TEXTURE_WIDTH) {
							posX = 0;
							posY += 16;
						}
					}
				}

			}
		}
	}

	private static @NotNull Button.OnPress clickOptions(IScreen screen) {
		return (onPress) -> {
			screen.client().execute(() -> {
				screen.client().setScreen(new TerrastorageOptionsScreen(screen.client().screen));
			});
		};
	}

	private static StorageButtonWidget getButtonWidget(int x, int y, int width, int height, Component message, Tooltip tooltipLoc, Button.OnPress action, int iconX, int iconY) {
		StorageButtonWidget storageButtonWidget = new StorageButtonWidget(x, y, width, height,
				message,
				ButtonsStyle.DEFAULT,
				action
		);
		storageButtonWidget.setTooltip(tooltipLoc);
		return ((IButton) storageButtonWidget).setIconCoords(iconX, iconY);
	}
	private static StorageButtonWidget getButtonWidget(int x, int y, Component message, Tooltip tooltipLoc, Button.OnPress action, int iconX, int iconY) {
		return getButtonWidget(x, y, 16, 16, message, tooltipLoc, action, iconX, iconY);
	}
	private static StorageButtonWidget editButtonWidget(StorageButtonWidget widget, int iconX, int iconY) {
		return ((IButton) widget).setIconCoords(iconX, iconY);
	}
}