public class ItemVenda {
    private String descricao;
    private double preco;
    private int quantidadeEstoque;

    public ItemVenda(String descricao, double preco, int quantidadeEstoque) {
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getPreco() {
        return preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public void setQuantidadeEstoque(int quantidadeEstoque) {
        this.quantidadeEstoque = quantidadeEstoque;
    }

    @Override
    public String toString() {
        return descricao + " - R$" + String.format("%.2f", preco) + " (Estoque: " + quantidadeEstoque + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ItemVenda itemVenda = (ItemVenda) obj;
        return descricao.equals(itemVenda.descricao);
    }

    @Override
    public int hashCode() {
        return descricao.hashCode();
    }
}
