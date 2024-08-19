package vt.statgainhelper.screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ItemSelectionScreen extends Screen {

    private TextFieldWidget searchField;
    private List<ItemStack> displayedItems;
    private List<ItemStack> filteredItems;
    private ItemStack selectedItem;
    private ButtonWidget addButton;
    private ButtonWidget confirmButton;
    private ButtonWidget cancelButton;
    private ItemGridWidget itemGridWidget;

    public ItemSelectionScreen() {
        super(Text.translatable("statgainhelper.itemselection"));
    }

    @Override
    protected void init() {
        int panelWidth = 100;

        FilterPanel filterPanel = new FilterPanel(this.width / 2 - 110 - panelWidth, 50, panelWidth, this.height - 120);
        this.addDrawableChild(filterPanel);

        this.searchField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 20, 200, 20, Text.literal(""));
        this.searchField.setChangedListener(this::onSearchTextChanged);
        this.addDrawableChild(this.searchField);

        this.displayedItems = new ArrayList<>(Registries.ITEM.stream().map(ItemStack::new).collect(Collectors.toList()));
        this.filteredItems = new ArrayList<>(this.displayedItems);

        this.itemGridWidget = new ItemGridWidget(this.width / 2 - 100, 50, 200, this.height - 120, 5);
        this.addSelectableChild(this.itemGridWidget);
        this.itemGridWidget.updateGrid(this.filteredItems);

        ItemInfoPanel itemInfoPanel = new ItemInfoPanel(this.width / 2 + 110, 50, panelWidth, this.height - 120);
        this.addDrawableChild(itemInfoPanel);

        this.addButton = ButtonWidget.builder(
                Text.translatable("statgainhelper.add"), button -> {
                    if (selectedItem != null) {
                        // Логика добавления предмета
                    }
                }).dimensions(this.width / 2 - 50, this.height - 60, 100, 20).build();

        this.confirmButton = ButtonWidget.builder(
                Text.translatable("statgainhelper.confirm"), button -> {
                    // Логика подтверждения
                }).dimensions(this.width / 2 - 105, this.height - 30, 100, 20).build();

        this.cancelButton = ButtonWidget.builder(
                        Text.translatable("statgainhelper.cancel"), button -> this.close())
                .dimensions(this.width / 2 + 5, this.height - 30, 100, 20).build();

        this.addDrawableChild(this.addButton);
        this.addDrawableChild(this.confirmButton);
        this.addDrawableChild(this.cancelButton);
    }


    private void onSearchTextChanged(String searchText) {
        this.filteredItems = this.displayedItems.stream()
                .filter(itemStack -> itemStack.getName().getString().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());
        this.itemGridWidget.updateGrid(this.filteredItems);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(context);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 10, 0xFFFFFF);
        this.searchField.render(context, mouseX, mouseY, delta);
        this.itemGridWidget.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
        this.renderTooltip(context, mouseX, mouseY);
    }

    private void renderTooltip(DrawContext context, int mouseX, int mouseY) {
        for (ItemGridWidget.Entry entry : this.itemGridWidget.children()) {
            if (entry.tooltipText != null) {
                int tooltipWidth = this.textRenderer.getWidth(entry.tooltipText.getString()) + 10;
                int tooltipHeight = 20;
                int tooltipX = entry.tooltipX + 12;
                int tooltipY = entry.tooltipY;

                context.fillGradient(RenderLayer.getGuiOverlay(), tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + tooltipHeight, 0x80000000, 0x80000000, 0);
                this.textRenderer.draw(entry.tooltipText.getString(), tooltipX + 5, tooltipY + 5, 0xFFFFFF, false, context.getMatrices().peek().getPositionMatrix(), context.getVertexConsumers(), TextRenderer.TextLayerType.SEE_THROUGH, 0, 0xF000F0, false);
                entry.tooltipText = null;
            }
        }
    }

    private class ItemGridWidget extends EntryListWidget<ItemGridWidget.Entry> {
        private int selectedItemIndex = -1;
        private final int columns;
        private List<ItemStack> items = new ArrayList<>();

        public ItemGridWidget(int x, int y, int width, int height, int columns) {
            super(ItemSelectionScreen.this.client, width, height, y, height + y, 20);
            this.columns = columns;
            this.left = x;
            this.right = x + width;
        }

        public void updateGrid(List<ItemStack> items) {
            this.items = items;
            this.clearEntries();
            int rows = (int) Math.ceil((double) items.size() / columns);
            for (int row = 0; row < rows; row++) {
                this.addEntry(new Entry(row));
            }
        }

        @Override
        protected void drawSelectionHighlight(DrawContext context, int y, int entryWidth, int entryHeight, int borderColor, int fillColor) {
            int startX = this.left + 10;
            int itemWidth = 40;

            int itemX = startX + (selectedItemIndex % columns) * itemWidth;

            context.fill(itemX - 2, y - 2, itemX + 20 + 2, y + 17 + 2, borderColor);
            context.fill(itemX - 1, y - 1, itemX + 20 + 1, y + 17 + 1, fillColor);
        }


        @Override
        public int getRowWidth() {
            return this.width;
        }

        @Override
        protected int getScrollbarPositionX() {
            return this.right - 6;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {

        }

        @Override
        public Optional<Element> hoveredElement(double mouseX, double mouseY) {
            return super.hoveredElement(mouseX, mouseY);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            return super.keyReleased(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            return super.charTyped(chr, modifiers);
        }

        @Override
        public void setFocused(boolean focused) {
            super.setFocused(focused);
        }

        @Override
        public boolean isFocused() {
            return super.isFocused();
        }

        @Override
        public @Nullable GuiNavigationPath getFocusedPath() {
            return super.getFocusedPath();
        }

        @Override
        public void focusOn(@Nullable Element element) {
            super.focusOn(element);
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
        }

        @Override
        public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
            return super.getNavigationPath(navigation);
        }

        private class Entry extends EntryListWidget.Entry<ItemGridWidget.Entry> {
            private final int row;
            private Text tooltipText;
            private int tooltipX;
            private int tooltipY;

            public Entry(int row) {
                this.row = row;
            }

            @Override
            public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
                int startIndex = row * columns;
                int endIndex = Math.min(startIndex + columns, items.size());

                for (int i = startIndex; i < endIndex; i++) {
                    ItemStack itemStack = items.get(i);
                    int itemX = x + (i - startIndex) * 40;
                    context.drawItem(itemStack, itemX + 10, y);
                    if (mouseX > itemX + 10 && mouseX < itemX + 30 && mouseY > y && mouseY < y + 20) {
                        tooltipText = itemStack.getName();
                        tooltipX = mouseX;
                        tooltipY = mouseY;
                        if (hovered && mouseX >= itemX && mouseX < itemX + 20) {
                            selectedItem = itemStack;
                        }
                    }
                }
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                int startIndex = row * columns;
                int endIndex = Math.min(startIndex + columns, items.size());

                for (int i = startIndex; i < endIndex; i++) {
                    int itemX = ItemGridWidget.this.left + (i - startIndex) * 40 + 10;
                    int itemY = ItemGridWidget.this.getRowTop(row);
                    if (mouseX >= itemX && mouseX < itemX + 20 && mouseY >= itemY && mouseY < itemY + 20) {
                        selectedItem = items.get(i);
                        selectedItemIndex = i;
                        return true;
                    }
                }
                return false;
            }
        }
    }

    private class ItemInfoPanel implements Drawable,
            Element,
            Widget,
            Selectable {

        private int x, y, width, height;
        private SelectionType selectionType = SelectionType.NONE;

        public ItemInfoPanel(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            context.fill(x, y, x + width, y + height, 0xFF202020);

            if (itemGridWidget.selectedItemIndex != -1) {
                ItemStack selectedItem = itemGridWidget.items.get(itemGridWidget.selectedItemIndex);
                drawItemScaled(context, selectedItem, x + (float) width / 2, y + 18, 32, 32, 32);
                drawScrollableText(context, textRenderer, selectedItem.getName(), x + 10, y + 60, width - 20, height - 70);
                drawScrollableText(context, textRenderer, Text.literal(Registries.ITEM.getId(selectedItem.getItem()).toString()), x + 10, y + 80, width - 20, height - 70);
            } else {
                drawScrollableText(context, textRenderer, Text.translatable("statgainhelper.selectitem"), x + 10, y + 60, width - 20, height - 70);
            }
        }

        private void drawItemScaled(DrawContext context, ItemStack selectedItem, float x, float y, float scaleX, float scaleY, float scaleZ) {
            if (selectedItem != null && selectedItem.getItem() != null && client != null) {
                BakedModel bakedModel = client.getItemRenderer().getModel(selectedItem, null, null, 0);
                context.getMatrices().push();
                context.getMatrices().translate(x, y, 150);
                try {
                    context.getMatrices().multiplyPositionMatrix(new Matrix4f().scaling(1.0f, -1.0f, 1.0f));
                    context.getMatrices().scale(scaleX, scaleY, scaleZ);
                    boolean bl = !bakedModel.isSideLit();
                    if (bl) {
                        DiffuseLighting.disableGuiDepthLighting();
                    }
                    client.getItemRenderer().renderItem(selectedItem, ModelTransformationMode.GUI, false, context.getMatrices(), context.getVertexConsumers(), 0xF000F0, OverlayTexture.DEFAULT_UV, bakedModel);
                    if (bl) {
                        DiffuseLighting.enableGuiDepthLighting();
                    }
                } catch (Throwable throwable) {
                    CrashReport crashReport = CrashReport.create(throwable, "Rendering item");
                    CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
                    crashReportSection.add("Item Type", () -> String.valueOf(selectedItem.getItem()));
                    crashReportSection.add("Item Damage", () -> String.valueOf(selectedItem.getDamage()));
                    crashReportSection.add("Item NBT", () -> String.valueOf(selectedItem.getNbt()));
                    crashReportSection.add("Item Foil", () -> String.valueOf(selectedItem.hasGlint()));
                    throw new CrashException(crashReport);
                }
                context.getMatrices().pop();
            }
        }

        private void drawScrollableText(DrawContext context, TextRenderer textRenderer, Text text, int x, int y, int width, int height) {
            List<OrderedText> lines = textRenderer.wrapLines(text, width);
            for (int i = 0; i < lines.size(); i++) {
                context.drawCenteredTextWithShadow(textRenderer, lines.get(i), x + width / 2, y + i * 10, 0xFFFFFF);
            }
        }

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            Element.super.mouseMoved(mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return Element.super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return Element.super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            return Element.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            return Element.super.mouseScrolled(mouseX, mouseY, amount);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return Element.super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            return Element.super.keyReleased(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            return Element.super.charTyped(chr, modifiers);
        }

        @Override
        public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
            return Element.super.getNavigationPath(navigation);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return Element.super.isMouseOver(mouseX, mouseY);
        }

        @Override
        public void setFocused(boolean focused) {

        }

        @Override
        public boolean isFocused() {
            return false;
        }

        @Override
        public @Nullable GuiNavigationPath getFocusedPath() {
            return Element.super.getFocusedPath();
        }

        @Override
        public ScreenRect getNavigationFocus() {
            return Element.super.getNavigationFocus();
        }

        @Override
        public void setPosition(int x, int y) {
            Widget.super.setPosition(x, y);
        }

        @Override
        public SelectionType getType() {
            return selectionType;
        }

        @Override
        public boolean isNarratable() {
            return Selectable.super.isNarratable();
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {

        }

        @Override
        public void setX(int x) {
            this.x = x;
        }

        @Override
        public void setY(int y) {
            this.y = y;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void forEachChild(Consumer<ClickableWidget> consumer) {

        }

        @Override
        public int getNavigationOrder() {
            return Element.super.getNavigationOrder();
        }
    }

    private static class FilterPanel implements Drawable,
            Element,
            Widget,
            Selectable {
        private int x;
        private int y;
        private int width;
        private int height;
        private SelectionType selectionType = SelectionType.NONE;

        public FilterPanel(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }


        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            context.fill(x, y, x + width, y + height, 0xFF202020); // Темный фон
            // Добавить рендеринг кнопок для фильтров и другие элементы UI
        }

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            Element.super.mouseMoved(mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return Element.super.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return Element.super.mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            return Element.super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
            return Element.super.mouseScrolled(mouseX, mouseY, amount);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return Element.super.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            return Element.super.keyReleased(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            return Element.super.charTyped(chr, modifiers);
        }

        @Override
        public @Nullable GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
            return Element.super.getNavigationPath(navigation);
        }

        @Override
        public boolean isMouseOver(double mouseX, double mouseY) {
            return Element.super.isMouseOver(mouseX, mouseY);
        }

        @Override
        public void setFocused(boolean focused) {

        }

        @Override
        public boolean isFocused() {
            return false;
        }

        @Override
        public @Nullable GuiNavigationPath getFocusedPath() {
            return Element.super.getFocusedPath();
        }

        @Override
        public ScreenRect getNavigationFocus() {
            return Element.super.getNavigationFocus();
        }

        @Override
        public void setPosition(int x, int y) {
            Widget.super.setPosition(x, y);
        }

        @Override
        public SelectionType getType() {
            return selectionType;
        }

        @Override
        public boolean isNarratable() {
            return Selectable.super.isNarratable();
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {

        }

        @Override
        public void setX(int x) {
            this.x = x;
        }

        @Override
        public void setY(int y) {
            this.y = y;
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public void forEachChild(Consumer<ClickableWidget> consumer) {
        }

        @Override
        public int getNavigationOrder() {
            return Element.super.getNavigationOrder();
        }
    }
}
