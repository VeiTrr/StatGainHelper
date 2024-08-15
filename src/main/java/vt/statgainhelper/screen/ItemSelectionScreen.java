package vt.statgainhelper.screen;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.EntryListWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
        this.searchField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 20, 200, 20, Text.literal(""));
        this.searchField.setChangedListener(this::onSearchTextChanged);
        this.addDrawableChild(this.searchField);

        this.displayedItems = new ArrayList<>(Registries.ITEM.stream().map(ItemStack::new).collect(Collectors.toList()));
        this.filteredItems = new ArrayList<>(this.displayedItems);

        this.itemGridWidget = new ItemGridWidget(this.width / 2 - 100, 50, 200, this.height - 120, 5);
        this.addSelectableChild(this.itemGridWidget);

        this.itemGridWidget.updateGrid(this.filteredItems);

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
                entry.tooltipText = null; // Clear tooltip after rendering
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
            int startX = this.left + 10; // Начальная X позиция для первой колонки
            int itemWidth = 40; // Ширина ячейки предмета (включая отступы)

            // Вычисляем позицию выбранного предмета в строке
            int itemX = startX + (selectedItemIndex % columns) * itemWidth;

            // Отрисовываем выделение только вокруг выбранного предмета
            context.fill(itemX - 2, y - 2, itemX + 20 + 2, y + 20 + 2, borderColor);
            context.fill(itemX - 1, y - 1, itemX + 20 + 1, y + 20 + 1, fillColor);
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
                    int itemY = ItemGridWidget.this.getRowTop(row);  // Используем метод getRowTop() для вычисления Y координаты строки
                    if (mouseX >= itemX && mouseX < itemX + 20 && mouseY >= itemY && mouseY < itemY + 20) {
                        selectedItem = items.get(i);
                        selectedItemIndex = i;  // Обновляем индекс выбранного предмета
                        return true;
                    }
                }
                return false;
            }
        }
    }
}
