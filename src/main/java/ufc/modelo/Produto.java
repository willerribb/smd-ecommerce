package ufc.modelo;

import java.math.BigDecimal;


public class Produto {

    private int id;
    private String descricao;
    private BigDecimal preco;
    private int quantidade;     // quantidade em estoque
    private String foto;        // caminho ou URL da imagem
    private Categoria categoria;

    public Produto() {
    }

    public Produto(String descricao, BigDecimal preco, int quantidade,
                   String foto, Categoria categoria) {
        this.descricao = descricao;
        setPreco(preco);
        setQuantidade(quantidade);
        this.foto = foto;
        this.categoria = categoria;
    }

    public Produto(int id, String descricao, BigDecimal preco, int quantidade,
                   String foto, Categoria categoria) {
        this(descricao, preco, quantidade, foto, categoria);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        if (preco == null || preco.signum() < 0) {
            throw new IllegalArgumentException("O preço não pode ser nulo nem negativo.");
        }
        this.preco = preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("A quantidade em estoque não pode ser negativa.");
        }
        this.quantidade = quantidade;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public boolean temEstoque(int quantidadePedida) {
        return quantidadePedida > 0 && quantidade >= quantidadePedida;
    }

    public void baixarEstoque(int quantidadeVendida) {
        if (!temEstoque(quantidadeVendida)) {
            throw new IllegalStateException("Estoque insuficiente para o produto: " + descricao);
        }
        this.quantidade -= quantidadeVendida;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produto)) return false;
        Produto outro = (Produto) o;
        return id != 0 && id == outro.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    @Override
    public String toString() {
        return "Produto{id=" + id + ", descricao='" + descricao + "', preco=" + preco
                + ", quantidade=" + quantidade + ", categoria="
                + (categoria != null ? categoria.getNome() : "nenhuma") + "}";
    }
}
