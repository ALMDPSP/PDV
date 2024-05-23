import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

class Item {
    private String nome;
    private double preco;

    public Item(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }
}

class ItemSelecionado {
    private Item item;
    private int quantidade;

    public ItemSelecionado(Item item, int quantidade) {
        this.item = item;
        this.quantidade = quantidade;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPrecoTotal() {
        return item.getPreco() * quantidade;
    }

    @Override
    public String toString() {
        return item.getNome() + " (x" + quantidade + ") - R$" + String.format("%.2f", getPrecoTotal());
    }
}

public class AnotacaoGUI extends Application {

    private List<Item> itensDisponiveis = new ArrayList<>();
    private List<ItemSelecionado> itensSelecionados = new ArrayList<>();
    private double subtotal = 0;
    private String clienteNome = "";

    private TextField clienteTextField;
    private ListView<String> listView;
    private ListView<String> itensSelecionadosListView;
    private Label subtotalLabel;
    private TextField searchField;
    private TextField quantidadeTextField;

    @Override
    public void start(Stage primaryStage) {
        carregarItensDisponiveis("C:\\Programas\\Casca de bala\\itensDisponiveis.txt");

        primaryStage.setTitle("Sistema de Anotação Developer by Alexandre Martins (11)987292231 version 1.1.0");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        clienteTextField = new TextField();
        clienteTextField.setPromptText("Nome do Cliente");

        searchField = new TextField();
        searchField.setPromptText("Pesquisar...");

        listView = new ListView<>();
        atualizarListViewItensDisponiveis("");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            atualizarListViewItensDisponiveis(newValue);
        });

        itensSelecionadosListView = new ListView<>();

        HBox adicionarBox = new HBox(10);
        adicionarBox.setPadding(new Insets(10, 0, 10, 0));

        quantidadeTextField = new TextField();
        quantidadeTextField.setPromptText("Quantidade");
        quantidadeTextField.setPrefWidth(80);

        Button adicionarButton = new Button("Adicionar Item");
        adicionarButton.setOnAction(e -> {
            String selectedItem = listView.getSelectionModel().getSelectedItem();
            String quantidadeText = quantidadeTextField.getText();

            if (selectedItem != null && !quantidadeText.isEmpty() && quantidadeText.matches("\\d+")) {
                int quantidade = Integer.parseInt(quantidadeText);
                String[] parts = selectedItem.split(" - R\\$");
                String itemName = parts[0];
                double itemPrice = Double.parseDouble(parts[1]);
                Item item = new Item(itemName, itemPrice);
                ItemSelecionado itemSelecionado = new ItemSelecionado(item, quantidade);

                itensSelecionados.add(itemSelecionado);
                subtotal += itemSelecionado.getPrecoTotal();
                itensSelecionadosListView.getItems().add(itemSelecionado.toString());
                atualizarSubtotal();
            } else {
                exibirAlerta("Entrada inválida", "Por favor, selecione um item e insira uma quantidade válida.");
            }
        });

        adicionarBox.getChildren().addAll(quantidadeTextField, adicionarButton);

        Button excluirButton = new Button("Excluir Item");
        excluirButton.setOnAction(e -> {
            String selectedItem = itensSelecionadosListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                ItemSelecionado itemToRemove = null;
                for (ItemSelecionado item : itensSelecionados) {
                    if (item.toString().equals(selectedItem)) {
                        itemToRemove = item;
                        break;
                    }
                }
                if (itemToRemove != null) {
                    itensSelecionados.remove(itemToRemove);
                    subtotal -= itemToRemove.getPrecoTotal();
                    itensSelecionadosListView.getItems().remove(selectedItem);
                    atualizarSubtotal();
                }
            } else {
                exibirAlerta("Nenhum item selecionado", "Por favor, selecione um item da lista para excluir.");
            }
        });

        Button salvarPedidoButton = new Button("Salvar Pedido");
        salvarPedidoButton.setOnAction(e -> {
            salvarPedido();
            mostrarPedido();
        });

        Button novaAnotacaoButton = new Button("Nova Anotação");
        novaAnotacaoButton.setOnAction(e -> {
            abrirNovaAnotacao();
        });

        subtotalLabel = new Label("Subtotal: R$0.00");

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(5);

        vbox.getChildren().addAll(clienteTextField, searchField, listView, adicionarBox, itensSelecionadosListView,
                subtotalLabel, excluirButton, salvarPedidoButton, novaAnotacaoButton, gridPane);

        Scene scene = new Scene(vbox, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void carregarItensDisponiveis(String caminhoArquivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(caminhoArquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] partes = linha.split(",");
                String nome = partes[0];
                double preco = Double.parseDouble(partes[1]);
                itensDisponiveis.add(new Item(nome, preco));
            }
        } catch (IOException e) {
            exibirAlerta("Erro", "Erro ao carregar itens disponíveis: " + e.getMessage());
        }
    }

    private void atualizarListViewItensDisponiveis(String filter) {
        listView.getItems().clear();
        for (Item item : itensDisponiveis) {
            if (item.getNome().toLowerCase().contains(filter.toLowerCase())) {
                listView.getItems().add(item.getNome() + " - R$" + item.getPreco());
            }
        }
    }

    private void atualizarSubtotal() {
        subtotalLabel.setText("Subtotal: R$" + String.format("%.2f", subtotal));
    }

    private void salvarPedido() {
        clienteNome = clienteTextField.getText();
        if (clienteNome.isEmpty()) {
            exibirAlerta("Erro", "Por favor, insira o nome do cliente antes de salvar o pedido.");
            return;
        }

        StringBuilder pedido = new StringBuilder("Cliente: " + clienteNome + "\n\nItens:\n");
        for (ItemSelecionado item : itensSelecionados) {
            pedido.append(item.toString()).append("\n");
        }
        pedido.append("\nSubtotal: R$").append(String.format("%.2f", subtotal));

        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter("C:\\Programas\\Casca de bala\\anotacoes.txt", true))) {
            writer.write(pedido.toString());
            writer.newLine();
            exibirAlerta("Pedido Salvo", "O pedido foi salvo com sucesso.");
        } catch (IOException e) {
            exibirAlerta("Erro", "Erro ao salvar o pedido: " + e.getMessage());
        }
    }

    private void mostrarPedido() {
        Stage mostrarStage = new Stage();
        mostrarStage.setTitle("Pedido Salvo");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        Label clienteLabel = new Label("Cliente: " + clienteNome);
        ListView<String> mostrarListView = new ListView<>();
        for (ItemSelecionado item : itensSelecionados) {
            mostrarListView.getItems().add(item.toString());
        }

        vbox.getChildren().addAll(clienteLabel, mostrarListView);

        Scene scene = new Scene(vbox, 500, 400);
        mostrarStage.setScene(scene);
        mostrarStage.show();
    }

    private void abrirNovaAnotacao() {
        Stage novaAnotacaoStage = new Stage();
        novaAnotacaoStage.setTitle("Nova Anotação");

        AnotacaoGUI novaAnotacao = new AnotacaoGUI();
        novaAnotacao.start(novaAnotacaoStage);
    }

    private void exibirAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
