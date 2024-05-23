import java.awt.*;
import java.io.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class CascaDeBalaGUI extends JFrame {
    private DefaultListModel<ItemVenda> produtosListModel;
    private DefaultListModel<ItemVenda> carrinhoListModel;
    private JLabel subtotalLabel;
    private JLabel produtoSelecionadoLabel;
    private JLabel descricaoLabel;
    private JSpinner quantidadeSpinner;
    private JTextField pesquisaField;

    private List<ItemVenda> itensDisponiveis;
    private List<ItemVenda> itensVendidos;

    public CascaDeBalaGUI() {
        setTitle("Adega Casca de Bala - Developed by ALEXANDRE MARTINS (11)987292231 version 1.1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizar a janela ao iniciar
        setLocationRelativeTo(null);

        produtosListModel = new DefaultListModel<>();
        carrinhoListModel = new DefaultListModel<>();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(240, 240, 240)); // Cor de fundo personalizada

        ImageIcon logoIcon = new ImageIcon("C:\\Programas\\Casca de bala\\CASCA DE BALA.jpg");
        Image imagemRedimensionada = logoIcon.getImage().getScaledInstance(100, -1, Image.SCALE_SMOOTH);
        ImageIcon logoRedimensionado = new ImageIcon(imagemRedimensionada);
        JLabel logoLabel = new JLabel(logoRedimensionado);
        mainPanel.add(logoLabel, BorderLayout.NORTH);

        JList<ItemVenda> produtosList = new JList<>(produtosListModel);
        produtosList.setFont(new Font("Arial", Font.PLAIN, 14));
        produtosList.setBackground(Color.WHITE);
        produtosList.setSelectionBackground(Color.LIGHT_GRAY);
        produtosList.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        produtosList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane produtosScrollPane = new JScrollPane(produtosList);
        produtosList.setCellRenderer(new ItemVendaCellRenderer()); // Definindo o renderizador de células
        produtosList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ItemVenda selectedItem = produtosList.getSelectedValue();
                if (selectedItem != null) {
                    produtoSelecionadoLabel
                            .setText("<html><b>Produto Selecionado:</b> " + selectedItem.getDescricao() + "</html>");
                    descricaoLabel.setText(selectedItem.getDescricao());
                }
            }
        });
        mainPanel.add(produtosScrollPane, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(new Color(240, 240, 240)); // Cor de fundo personalizada
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);

        produtoSelecionadoLabel = new JLabel("<html><b>Nenhum produto selecionado</b></html>");
        produtoSelecionadoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        infoPanel.add(produtoSelecionadoLabel, gbc);

        descricaoLabel = new JLabel();
        descricaoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridy++;
        infoPanel.add(descricaoLabel, gbc);

        gbc.gridy++;
        JLabel quantidadeLabel = new JLabel("Quantidade:");
        quantidadeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        infoPanel.add(quantidadeLabel, gbc);

        gbc.gridx++;
        SpinnerModel spinnerModel = new SpinnerNumberModel(1, 1, 10, 1);
        quantidadeSpinner = new JSpinner(spinnerModel);
        infoPanel.add(quantidadeSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        JButton adicionarButton = new JButton("Adicionar ao Carrinho");
        adicionarButton.addActionListener(e -> adicionarAoCarrinho(produtosList));
        adicionarButton.setFont(new Font("Arial", Font.BOLD, 16));
        adicionarButton.setBackground(Color.ORANGE); // Cor de fundo personalizada
        adicionarButton.setForeground(Color.WHITE); // Cor do texto
        infoPanel.add(adicionarButton, gbc);

        pesquisaField = new JTextField(20);
        gbc.gridy++;
        infoPanel.add(pesquisaField, gbc);

        JButton pesquisarButton = new JButton("Pesquisar");
        pesquisarButton.addActionListener(e -> pesquisarItem(pesquisaField.getText()));
        gbc.gridx++;
        infoPanel.add(pesquisarButton, gbc);

        // Botão para editar o item selecionado
        JButton editarItemButton = new JButton("Editar Item");
        editarItemButton.addActionListener(e -> editarItem(produtosList));
        gbc.gridy++;
        gbc.gridx = 0;
        infoPanel.add(editarItemButton, gbc);

        mainPanel.add(infoPanel, BorderLayout.EAST);

        JList<ItemVenda> carrinhoList = new JList<>(carrinhoListModel);
        carrinhoList.setFont(new Font("Arial", Font.PLAIN, 14));
        JScrollPane carrinhoScrollPane = new JScrollPane(carrinhoList);
        mainPanel.add(carrinhoScrollPane, BorderLayout.WEST);

        subtotalLabel = new JLabel("Subtotal: R$0.00");
        subtotalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(subtotalLabel, BorderLayout.SOUTH);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(new Color(200, 200, 200)); // Cor de fundo personalizada

        JButton finalizarButton = new JButton("Finalizar Venda");
        JButton cadastrarNovoItemButton = new JButton("Cadastrar Novo Item");
        JButton limparCarrinhoButton = new JButton("Limpar Carrinho");

        finalizarButton.addActionListener(e -> finalizarVenda());
        cadastrarNovoItemButton.addActionListener(e -> cadastrarNovoItem());
        limparCarrinhoButton.addActionListener(e -> limparCarrinho());

        finalizarButton.setFont(new Font("Arial", Font.BOLD, 18));
        cadastrarNovoItemButton.setFont(new Font("Arial", Font.BOLD, 18));
        limparCarrinhoButton.setFont(new Font("Arial", Font.BOLD, 18));

        finalizarButton.setBackground(Color.GREEN);
        cadastrarNovoItemButton.setBackground(Color.BLUE);
        limparCarrinhoButton.setBackground(Color.RED);

        finalizarButton.setForeground(Color.WHITE);
        cadastrarNovoItemButton.setForeground(Color.WHITE);
        limparCarrinhoButton.setForeground(Color.WHITE);

        footerPanel.add(finalizarButton);
        footerPanel.add(cadastrarNovoItemButton);
        footerPanel.add(limparCarrinhoButton);

        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);

        inicializarItens();
        atualizarListaDeProdutos();
    }

    private void inicializarItens() {
        itensDisponiveis = new ArrayList<>();
        itensVendidos = new ArrayList<>();
        carregarItensDisponiveis();
    }

    private void carregarItensDisponiveis() {
        File file = new File("C:\\Programas\\Casca de bala\\itensDisponiveis.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String descricao = parts[0];
                        double preco = Double.parseDouble(parts[1]);
                        int quantidadeEstoque = Integer.parseInt(parts[2]);
                        ItemVenda item = new ItemVenda(descricao, preco, quantidadeEstoque);
                        itensDisponiveis.add(item);
                    }
                }
            } catch (IOException | NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Erro ao carregar os itens disponíveis.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void atualizarListaDeProdutos() {
        produtosListModel.clear();
        for (ItemVenda item : itensDisponiveis) {
            produtosListModel.addElement(item);
        }
    }

    private void adicionarAoCarrinho(JList<ItemVenda> produtosList) {
        ItemVenda selectedItem = produtosList.getSelectedValue();
        if (selectedItem != null) {
            int quantidade = (int) quantidadeSpinner.getValue();
            if (quantidade <= selectedItem.getQuantidadeEstoque()) {
                // Consumir estoque
                consumirEstoque(selectedItem, quantidade);

                for (int i = 0; i < quantidade; i++) {
                    itensVendidos.add(selectedItem);
                    carrinhoListModel.addElement(selectedItem);
                }

                // Calcular o subtotal após adicionar os itens ao carrinho
                double subtotal = calcularSubtotal();

                // Formatar o subtotal para exibição
                DecimalFormat df = new DecimalFormat("#0.00");
                String subtotalFormatado = df.format(subtotal);

                // Exibir uma mensagem com o subtotal atualizado
                JOptionPane.showMessageDialog(this, "Subtotal atualizado: R$" + subtotalFormatado);

                // Atualizar o subtotal na interface
                atualizarSubtotal();

                // Atualizar o arquivo de estoque
                atualizarArquivoDeEstoque();
            } else {
                JOptionPane.showMessageDialog(this, "Estoque insuficiente.", "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um produto.", "Produto não selecionado",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void limparCarrinho() {
        carrinhoListModel.clear();
        itensVendidos.clear();
        atualizarSubtotal();
    }

    private void cadastrarNovoItem() {
        JTextField descricaoField = new JTextField();
        JTextField precoField = new JTextField();
        JTextField quantidadeEstoqueField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Descrição:"));
        panel.add(descricaoField);
        panel.add(new JLabel("Preço:"));
        panel.add(precoField);
        panel.add(new JLabel("Quantidade em Estoque:"));
        panel.add(quantidadeEstoqueField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Cadastrar Novo Item",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String descricao = descricaoField.getText();
                double preco = Double.parseDouble(precoField.getText());
                int quantidadeEstoque = Integer.parseInt(quantidadeEstoqueField.getText());

                ItemVenda novoItem = new ItemVenda(descricao, preco, quantidadeEstoque);
                itensDisponiveis.add(novoItem);
                produtosListModel.addElement(novoItem);

                // Salvar o novo item no arquivo
                File file = new File("C:\\Programas\\Casca de bala\\itensDisponiveis.txt");
                try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
                    writer.println(descricao + "," + preco + "," + quantidadeEstoque);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Erro ao salvar o novo item no arquivo.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Dados inválidos. Por favor, verifique os valores inseridos.",
                        "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarItem(JList<ItemVenda> produtosList) {
        ItemVenda selectedItem = produtosList.getSelectedValue();
        if (selectedItem != null) {
            JTextField descricaoField = new JTextField(selectedItem.getDescricao());
            JTextField precoField = new JTextField(String.valueOf(selectedItem.getPreco()));
            JTextField quantidadeEstoqueField = new JTextField(String.valueOf(selectedItem.getQuantidadeEstoque()));

            JPanel panel = new JPanel(new GridLayout(0, 1));
            panel.add(new JLabel("Descrição:"));
            panel.add(descricaoField);
            panel.add(new JLabel("Preço:"));
            panel.add(precoField);
            panel.add(new JLabel("Quantidade em Estoque:"));
            panel.add(quantidadeEstoqueField);

            int result = JOptionPane.showConfirmDialog(null, panel, "Editar Item",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    String descricao = descricaoField.getText();
                    double preco = Double.parseDouble(precoField.getText());
                    int quantidadeEstoque = Integer.parseInt(quantidadeEstoqueField.getText());

                    // Atualizar os campos do item selecionado
                    selectedItem.setDescricao(descricao);
                    selectedItem.setPreco(preco);
                    selectedItem.setQuantidadeEstoque(quantidadeEstoque);

                    // Atualizar a exibição do item na lista
                    produtosList.repaint();

                    // Atualizar o arquivo de estoque
                    atualizarArquivoDeEstoque();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Dados inválidos. Por favor, verifique os valores inseridos.",
                            "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um produto.", "Produto não selecionado",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void pesquisarItem(String termo) {
        produtosListModel.clear();
        for (ItemVenda item : itensDisponiveis) {
            if (item.getDescricao().toLowerCase().contains(termo.toLowerCase())) {
                produtosListModel.addElement(item);
            }
        }
    }

    private void atualizarArquivoDeEstoque() {
        File file = new File("C:\\Programas\\Casca de bala\\itensDisponiveis.txt");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (ItemVenda item : itensDisponiveis) {
                writer.println(item.getDescricao() + "," + item.getPreco() + "," + item.getQuantidadeEstoque());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar o arquivo de estoque.", "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void consumirEstoque(ItemVenda item, int quantidade) {
        for (ItemVenda i : itensDisponiveis) {
            if (i.equals(item)) {
                i.setQuantidadeEstoque(i.getQuantidadeEstoque() - quantidade);
                break;
            }
        }
    }

    private double calcularSubtotal() {
        double subtotal = 0.0;
        for (ItemVenda item : itensVendidos) {
            subtotal += item.getPreco();
        }
        return subtotal;
    }

    private void atualizarSubtotal() {
        double subtotal = calcularSubtotal();
        DecimalFormat df = new DecimalFormat("#0.00");
        subtotalLabel.setText("Subtotal: R$" + df.format(subtotal));
    }

    private String selecionarMetodoPagamento() {
        String[] opcoes = { "Dinheiro", "Cartão de Crédito", "Cartão de Débito", "PIX" };
        return (String) JOptionPane.showInputDialog(this, "Selecione o método de pagamento:", "Método de Pagamento",
                JOptionPane.PLAIN_MESSAGE, null, opcoes, opcoes[0]);
    }

    private void finalizarVenda() {
        if (!itensVendidos.isEmpty()) {
            double subtotal = calcularSubtotal();
            String metodoPagamento = selecionarMetodoPagamento();

            if (metodoPagamento != null) {
                if (metodoPagamento.equals("Dinheiro")) {
                    String valorPagoStr = JOptionPane.showInputDialog(this, "Digite o valor pago pelo cliente:");
                    try {
                        double valorPago = Double.parseDouble(valorPagoStr);
                        if (valorPago >= subtotal) {
                            double troco = valorPago - subtotal;
                            DecimalFormat df = new DecimalFormat("#0.00");
                            JOptionPane.showMessageDialog(this, "Troco: R$" + df.format(troco), "Troco",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, "Valor pago insuficiente.", "Erro",
                                    JOptionPane.ERROR_MESSAGE);
                            return; // Não prosseguir com a venda
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Valor pago inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                        return; // Não prosseguir com a venda
                    }
                }

                LocalDateTime agora = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                String dataHora = agora.format(formatter);

                StringBuilder mensagemFinal = new StringBuilder();
                mensagemFinal.append("Data/Hora: ").append(dataHora).append("\n");

                // Adicionar detalhes de cada item vendido ao relatório e ao arquivo de log
                String historicoFilePath = "C:\\Programas\\Casca de bala\\historico_vendas.txt";
                try (PrintWriter writer = new PrintWriter(new FileWriter(historicoFilePath, true))) {
                    for (ItemVenda item : itensVendidos) {
                        mensagemFinal.append("Item: ").append(item.getDescricao()).append(" - Preço: R$")
                                .append(item.getPreco()).append("\n");
                        writer.println(dataHora + "," + item.getDescricao() + "," + item.getPreco());
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, "Erro ao salvar o histórico de vendas.", "Erro",
                            JOptionPane.ERROR_MESSAGE);
                }

                mensagemFinal.append("Método de Pagamento: ").append(metodoPagamento).append("\n");
                mensagemFinal.append("Subtotal: R$").append(subtotal).append("\n");
                JOptionPane.showMessageDialog(this, mensagemFinal.toString(), "Venda Finalizada",
                        JOptionPane.INFORMATION_MESSAGE);

                // Limpar o carrinho e atualizar o subtotal após a finalização da venda
                limparCarrinho();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Carrinho vazio.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CascaDeBalaGUI::new);
    }
}
