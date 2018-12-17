package com.management.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import org.springframework.data.elasticsearch.annotations.Document;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "nom")
    private String nom;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "product_provider",
               joinColumns = @JoinColumn(name = "products_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "providers_id", referencedColumnName = "id"))
    private Set<Provider> providers = new HashSet<>();

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinTable(name = "product_client",
               joinColumns = @JoinColumn(name = "products_id", referencedColumnName = "id"),
               inverseJoinColumns = @JoinColumn(name = "clients_id", referencedColumnName = "id"))
    private Set<Client> clients = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public Product nom(String nom) {
        this.nom = nom;
        return this;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Set<Provider> getProviders() {
        return providers;
    }

    public Product providers(Set<Provider> providers) {
        this.providers = providers;
        return this;
    }

    public Product addProvider(Provider provider) {
        this.providers.add(provider);
        provider.getProdcuts().add(this);
        return this;
    }

    public Product removeProvider(Provider provider) {
        this.providers.remove(provider);
        provider.getProdcuts().remove(this);
        return this;
    }

    public void setProviders(Set<Provider> providers) {
        this.providers = providers;
    }

    public Set<Client> getClients() {
        return clients;
    }

    public Product clients(Set<Client> clients) {
        this.clients = clients;
        return this;
    }

    public Product addClient(Client client) {
        this.clients.add(client);
        client.getProdcuts().add(this);
        return this;
    }

    public Product removeClient(Client client) {
        this.clients.remove(client);
        client.getProdcuts().remove(this);
        return this;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Product product = (Product) o;
        if (product.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), product.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", nom='" + getNom() + "'" +
            "}";
    }
}
