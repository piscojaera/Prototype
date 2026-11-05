package pedidos;

import java.util.HashMap;
import java.util.Map;

public class CatalogoDePedidos {
    private Map<String, Pedido> modelos = new HashMap<>();

    public void registrar(String chave, Pedido modelo) {
        modelos.put(chave, modelo);
    }

    public Pedido criar(String chave) {
        Pedido modelo = modelos.get(chave);

        if (modelo == null) {
            throw new IllegalArgumentException("Pedido não encontrado: " + chave);
        }

        return modelo.clonar();
    }
}
