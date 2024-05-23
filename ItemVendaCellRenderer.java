import javax.swing.*;
import java.awt.*;

public class ItemVendaCellRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
            boolean cellHasFocus) {
        Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof ItemVenda) {
            ItemVenda item = (ItemVenda) value;
            setText("<html>" + item.getDescricao() + " - <b>R$" + String.format("%.2f", item.getPreco())
                    + "</b> (Estoque: " + item.getQuantidadeEstoque() + ")</html>");
        }
        return c;
    }
}
