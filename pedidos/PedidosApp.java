package pedidos;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class PedidosApp {
    public static void main(String[] args) {
        Pedido modelo = new Pedido("(modelo)", "-");
        modelo.adicionarItem(new ItemPedido("Café 500g", 10, new BigDecimal("18.90")));
        modelo.adicionarItem(new ItemPedido("Açúcar 1kg", 20, new BigDecimal("4.50")));
        modelo.adicionarItem(new ItemPedido("Filtro de papel", 5, new BigDecimal("7.20")));

        CatalogoDePedidos catalogo = new CatalogoDePedidos();
        catalogo.registrar("reposicao-mensal", modelo);

        Pedido pedidoA = catalogo.criar("reposicao-mensal");
        pedidoA.setCliente("Mercado Central");
        pedidoA.setEnderecoEntrega("Rua das Flores, 120");

        Pedido pedidoB = catalogo.criar("reposicao-mensal");
        pedidoB.setCliente("Padaria Boa Massa");
        pedidoB.setEnderecoEntrega("Av. Paraná, 800");

        for (ItemPedido item : pedidoB.getItens()) {
            if (item.getProduto().equals("Café 500g")) {
                item.setQuantidade(20);
            }
        }

        Pedido pedidoC = catalogo.criar("reposicao-mensal");
        pedidoC.setCliente("Restaurante do Porto");
        pedidoC.setEnderecoEntrega("Rua do Cais, 45");
        pedidoC.adicionarItem(new ItemPedido("Adoçante 100ml", 12, new BigDecimal("6.00")));

        imprimirPedido(modelo);
        imprimirPedido(pedidoA);
        imprimirPedido(pedidoB);
        imprimirPedido(pedidoC);

        verificarCA01(modelo, pedidoA, pedidoB, pedidoC);
        verificarCA02(modelo);
        verificarCA03(pedidoA, pedidoB, pedidoC);
        verificarCA04(modelo);
        verificarCA05(modelo, pedidoA, pedidoB, pedidoC);
        verificarCA06(catalogo);
        verificarCA07(modelo);

        medirClonagem(modelo);
    }

    private static void imprimirPedido(Pedido pedido) {
        NumberFormat formato = NumberFormat.getNumberInstance(Locale.forLanguageTag("pt-BR"));
        formato.setMinimumFractionDigits(2);
        formato.setMaximumFractionDigits(2);

        System.out.println(
                "Pedido #" + pedido.getId()
                + " | " + pedido.getCliente()
                + " | " + pedido.getEnderecoEntrega()
                + " | itens=" + pedido.getItens().size()
                + " | total=R$ " + formato.format(pedido.valorTotal())
        );
    }

    private static void imprimirResultado(String criterio, boolean passou) {
        System.out.println(criterio + ": " + (passou ? "OK" : "FALHA"));
    }

    private static void verificarCA01(Pedido modelo, Pedido a, Pedido b, Pedido c) {
        Set<Long> ids = new HashSet<>();
        ids.add(modelo.getId());
        ids.add(a.getId());
        ids.add(b.getId());
        ids.add(c.getId());

        imprimirResultado("CA-01 Os quatro pedidos possuem IDs diferentes", ids.size() == 4);
    }

    private static void verificarCA02(Pedido modelo) {
        imprimirResultado(
                "CA-02 O total do modelo continua R$ 315,00",
                modelo.valorTotal().compareTo(new BigDecimal("315.00")) == 0
        );
    }

    private static void verificarCA03(Pedido a, Pedido b, Pedido c) {
        boolean passou = a.valorTotal().compareTo(new BigDecimal("315.00")) == 0
                && b.valorTotal().compareTo(new BigDecimal("504.00")) == 0
                && c.valorTotal().compareTo(new BigDecimal("387.00")) == 0;

        imprimirResultado("CA-03 Os totais dos pedidos A, B e C estão corretos", passou);
    }

    private static void verificarCA04(Pedido modelo) {
        imprimirResultado("CA-04 O modelo continua com 3 itens", modelo.getItens().size() == 3);
    }

    private static void verificarCA05(Pedido modelo, Pedido a, Pedido b, Pedido c) {
        Pedido[] pedidos = {a, b, c};
        boolean passou = true;

        for (Pedido pedido : pedidos) {
            for (ItemPedido item : pedido.getItens()) {
                for (ItemPedido itemModelo : modelo.getItens()) {
                    if (item == itemModelo) {
                        passou = false;
                    }
                }
            }
        }

        for (int i = 0; i < pedidos.length; i++) {
            for (int j = i + 1; j < pedidos.length; j++) {
                for (ItemPedido itemA : pedidos[i].getItens()) {
                    for (ItemPedido itemB : pedidos[j].getItens()) {
                        if (itemA == itemB) {
                            passou = false;
                        }
                    }
                }
            }
        }

        imprimirResultado("CA-05 Os itens das cópias são independentes", passou);
    }

    private static void verificarCA06(CatalogoDePedidos catalogo) {
        boolean passou = false;

        try {
            catalogo.criar("inexistente");
        } catch (IllegalArgumentException e) {
            passou = e.getMessage().contains("inexistente");
        }

        imprimirResultado("CA-06 Chave inexistente gera IllegalArgumentException", passou);
    }

    private static void verificarCA07(Pedido modelo) {
        imprimirResultado(
                "CA-07 O cliente do modelo permanece (modelo)",
                modelo.getCliente().equals("(modelo)")
        );
    }

    private static void medirClonagem(Pedido modelo) {
        int quantidade = 100000;
        long inicio = System.nanoTime();
        for (int i = 0; i < quantidade; i++) {
            modelo.clonar();
        }

        long fim = System.nanoTime();
        double tempoClonagem = (fim - inicio) / 1_000_000.0;

        inicio = System.nanoTime();

        for (int i = 0; i < quantidade; i++) {
            modelo.clonarPorSerializacao();
        }

        fim = System.nanoTime();
        double tempoSerializacao = (fim - inicio) / 1_000_000.0;

        System.out.printf(Locale.US, "Tempo de 100.000 cópias por clonar(): %.3f ms%n", tempoClonagem);
        System.out.printf(Locale.US, "Tempo de 100.000 cópias por clonarPorSerializacao(): %.3f ms%n", tempoSerializacao);
    }
}
