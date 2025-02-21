package jakesmythuk.terrastorageicons;

import me.timvinci.terrastorage.config.ClientConfigManager;
import me.timvinci.terrastorage.gui.TerrastorageOptionsScreen;
import me.timvinci.terrastorage.gui.widget.StorageButtonCreator;
import me.timvinci.terrastorage.gui.widget.StorageButtonWidget;
import me.timvinci.terrastorage.network.ClientNetworkHandler;
import me.timvinci.terrastorage.util.ButtonsPlacement;
import me.timvinci.terrastorage.util.ButtonsStyle;
import me.timvinci.terrastorage.util.LocalizedTextProvider;
import me.timvinci.terrastorage.util.StorageAction;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static jakesmythuk.terrastorageicons.TerrastorageIcons.*;

public class TerrastorageIconsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {

	}

	public static void renderButton(IButton button, DrawContext context, float delta) {
		StorageButtonWidget widget = ((StorageButtonWidget)(Object)button);
		if (widget.isSelected()) {
			button.selectedFrame(button.selectedFrame() + delta * 0.4f);
			if (button.selectedFrame() >= 3)
				button.selectedFrame(2);
		} else
			button.selectedFrame(0);

		if (ClientConfigManager.getInstance().getConfig().getButtonsStyle() == ButtonsStyle.DEFAULT) {
			context.drawTexture(ICONS_TEXTURE,
					button.getX(), button.getY(),
					16, 16,
					button.iconOffsetX(), button.iconOffsetY() + (button.getIconY() - button.iconOffsetY()) * 2,
					16, 16,
					TEXTURE_WIDTH, TEXTURE_HEIGHT);
		}
		context.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}


	public static void initScreen(HandledScreen<?> widget, IScreen screen) {
		if (!MinecraftClient.getInstance().player.isSpectator()) {
			if (widget.getScreenHandler().slots.size() - 36 >= 27) {
				boolean isEnderChest = false;
				if (widget.getScreenHandler() instanceof GenericContainerScreenHandler && widget.getTitle().equals(Text.translatable("container.enderchest"))) {
					isEnderChest = true;
				}

				StorageAction[] buttonActions = getButtonsActions(isEnderChest);
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
				Text buttonText;
				Tooltip buttonTooltip;
				StorageButtonWidget button;
				if (ClientConfigManager.getInstance().getConfig().getButtonsStyle() == ButtonsStyle.TEXT_ONLY) {
					_buttonActions = buttonActions;
					optionsButtonY = buttonActions.length;

					for(i = 0; i < optionsButtonY; ++i) {
						storageAction = _buttonActions[i];
						buttonText = (Text) LocalizedTextProvider.buttonTextCache.get(storageAction);
						buttonWidth = screen.textRenderer().getWidth(buttonText) + 6;
						buttonTooltip = (Tooltip)LocalizedTextProvider.buttonTooltipCache.get(storageAction);
						button = StorageButtonCreator.createStorageButton(storageAction, buttonX, buttonY, buttonWidth, buttonHeight, buttonText, buttonTooltip);
						screen.terrastorageIcons$addDrawableChild(button);
						buttonY += buttonHeight + buttonSpacing;
					}

					if (ClientConfigManager.getInstance().getConfig().getDisplayOptionsButton()) {
						int optionsButtonX = (screen.client().currentScreen.width - 120) / 2;
						optionsButtonY = screen.y() - 20;
						ButtonWidget optionsButtonWidget = getButtonWidget(optionsButtonX, optionsButtonY, 120, 15,
								Text.translatable("terrastorage.button.options"),
								Tooltip.of(Text.translatable("terrastorage.button.tooltip.options")), clickOptions(screen), 32, 16);
						//optionsButtonWidget.setTooltip(Tooltip.of(Text.translatable("terrastorage.button.tooltip.options")));
						screen.terrastorageIcons$addDrawableChild(optionsButtonWidget);
					}
				} else {
					_buttonActions = buttonActions;
					optionsButtonY = buttonActions.length;


					if (ClientConfigManager.getInstance().getConfig().getDisplayOptionsButton()) {
						//optionsButtonY = screen.y() - 20;
						ButtonWidget optionsButtonWidget = editButtonWidget(
								new StorageButtonWidget(buttonX, buttonY, buttonWidth, buttonHeight,
										Text.translatable("terrastorage.button.options"),
										Tooltip.of(Text.translatable("terrastorage.button.tooltip.options")), clickOptions(screen)),
								32, 16
						);
						screen.terrastorageIcons$addDrawableChild(optionsButtonWidget);
						buttonY += buttonHeight + buttonSpacing;
					}

					int posX = 0, posY = 0;
					for(i = 0; i < optionsButtonY; ++i) {
						storageAction = _buttonActions[i];
						buttonText = LocalizedTextProvider.buttonTextCache.get(storageAction);
						buttonTooltip = LocalizedTextProvider.buttonTooltipCache.get(storageAction);
						button = editButtonWidget(
								StorageButtonCreator.createStorageButton(storageAction, buttonX, buttonY, buttonWidth, buttonHeight, buttonText, buttonTooltip),
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

	private static ButtonWidget.@NotNull PressAction clickOptions(IScreen screen) {
		return (onPress) -> {
			screen.client().execute(() -> {
				screen.client().setScreen(new TerrastorageOptionsScreen(screen.client().currentScreen));
			});
		};
	}

	private static StorageButtonWidget getButtonWidget(int x, int y, int width, int height, Text message, Tooltip tooltipLoc, ButtonWidget.PressAction action, int iconX, int iconY) {
		return ((IButton) new StorageButtonWidget(x, y, width, height,
				message,
				tooltipLoc,
				action
		)).setIconCoords(iconX, iconY);
	}
	private static StorageButtonWidget getButtonWidget(int x, int y, Text message, Tooltip tooltipLoc, ButtonWidget.PressAction action, int iconX, int iconY) {
		return getButtonWidget(x, y, 16, 16, message, tooltipLoc, action, iconX, iconY);
	}
	private static StorageButtonWidget editButtonWidget(StorageButtonWidget widget, int iconX, int iconY) {
		return ((IButton) widget).setIconCoords(iconX, iconY);
	}

	private static StorageAction[] getButtonsActions(boolean isEnderChest) {
		StorageAction[] allActions = StorageAction.values();
		return Arrays.copyOf(allActions, allActions.length - (isEnderChest ? 2 : 1));
	}
}