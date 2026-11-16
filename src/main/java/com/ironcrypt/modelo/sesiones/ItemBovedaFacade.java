package com.ironcrypt.modelo.sesiones;

import com.ironcrypt.modelo.entidades.ItemBoveda;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class ItemBovedaFacade extends AbstractFacade<ItemBoveda> {

    @PersistenceContext(unitName = "IronCryptPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemBovedaFacade() {
        super(ItemBoveda.class);
    }

    public List<ItemBoveda> findByBoveda(Long bovedaId) {
        return em.createNamedQuery("ItemBoveda.findByBoveda", ItemBoveda.class)
                 .setParameter("bovedaId", bovedaId)
                 .getResultList();
    }
}
