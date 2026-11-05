package pedidos;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pedido implements Prototype<Pedido>, Serializable {
    private static final long serialVersionUID = 1L;
    private static long proximoId = 1;

    private long id;
    private String cliente;
    private String enderecoEntrega;
    private List<ItemPedido> itens;
    private LocalDate criadoEm;

    public Pedido(String cliente, String enderecoEntrega) {
        this.id = proximoId++;
        this.cliente = cliente;
        this.enderecoEntrega = enderecoEntrega;
        this.itens = new ArrayList<>();
        this.criadoEm = LocalDate.now();
    }

    public void adicionarItem(ItemPedido item) {
        itens.add(item);
    }

    @Override
    public Pedido clonar() {
        Pedido copia = new Pedido(cliente, enderecoEntrega);

        for (ItemPedido item : itens) {
            copia.adicionarItem(new ItemPedido(item));
        }

        return copia;
    }

    public Pedido clonarPorSerializacao() {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream saida = new ObjectOutputStream(bytes);
            saida.writeObject(this);
            saida.close();

            ByteArrayInputStream dados = new ByteArrayInputStream(bytes.toByteArray());
            ObjectInputStream entrada = new ObjectInputStream(dados);
            Pedido copia = (Pedido) entrada.readObject();
            entrada.close();

            copia.id = proximoId++;
            copia.criadoEm = LocalDate.now();

            return copia;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Erro ao copiar o pedido.", e);
        }
    }

    public BigDecimal valorTotal() {
        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedido item : itens) {
            total = total.add(item.subtotal());
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    public long getId() {
        return id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public LocalDate getCriadoEm() {
        return criadoEm;
    }
}
