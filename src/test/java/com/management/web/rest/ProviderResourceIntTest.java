package com.management.web.rest;

import com.management.GestionStockApp;

import com.management.domain.Provider;
import com.management.repository.ProviderRepository;
import com.management.repository.search.ProviderSearchRepository;
import com.management.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;


import static com.management.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ProviderResource REST controller.
 *
 * @see ProviderResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GestionStockApp.class)
public class ProviderResourceIntTest {

    private static final String DEFAULT_NOM = "AAAAAAAAAA";
    private static final String UPDATED_NOM = "BBBBBBBBBB";

    @Autowired
    private ProviderRepository providerRepository;

    /**
     * This repository is mocked in the com.management.repository.search test package.
     *
     * @see com.management.repository.search.ProviderSearchRepositoryMockConfiguration
     */
    @Autowired
    private ProviderSearchRepository mockProviderSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restProviderMockMvc;

    private Provider provider;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProviderResource providerResource = new ProviderResource(providerRepository, mockProviderSearchRepository);
        this.restProviderMockMvc = MockMvcBuilders.standaloneSetup(providerResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Provider createEntity(EntityManager em) {
        Provider provider = new Provider()
            .nom(DEFAULT_NOM);
        return provider;
    }

    @Before
    public void initTest() {
        provider = createEntity(em);
    }

    @Test
    @Transactional
    public void createProvider() throws Exception {
        int databaseSizeBeforeCreate = providerRepository.findAll().size();

        // Create the Provider
        restProviderMockMvc.perform(post("/api/providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(provider)))
            .andExpect(status().isCreated());

        // Validate the Provider in the database
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeCreate + 1);
        Provider testProvider = providerList.get(providerList.size() - 1);
        assertThat(testProvider.getNom()).isEqualTo(DEFAULT_NOM);

        // Validate the Provider in Elasticsearch
        verify(mockProviderSearchRepository, times(1)).save(testProvider);
    }

    @Test
    @Transactional
    public void createProviderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = providerRepository.findAll().size();

        // Create the Provider with an existing ID
        provider.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restProviderMockMvc.perform(post("/api/providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(provider)))
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeCreate);

        // Validate the Provider in Elasticsearch
        verify(mockProviderSearchRepository, times(0)).save(provider);
    }

    @Test
    @Transactional
    public void getAllProviders() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        // Get all the providerList
        restProviderMockMvc.perform(get("/api/providers?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(provider.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM.toString())));
    }
    
    @Test
    @Transactional
    public void getProvider() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        // Get the provider
        restProviderMockMvc.perform(get("/api/providers/{id}", provider.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(provider.getId().intValue()))
            .andExpect(jsonPath("$.nom").value(DEFAULT_NOM.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProvider() throws Exception {
        // Get the provider
        restProviderMockMvc.perform(get("/api/providers/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProvider() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        int databaseSizeBeforeUpdate = providerRepository.findAll().size();

        // Update the provider
        Provider updatedProvider = providerRepository.findById(provider.getId()).get();
        // Disconnect from session so that the updates on updatedProvider are not directly saved in db
        em.detach(updatedProvider);
        updatedProvider
            .nom(UPDATED_NOM);

        restProviderMockMvc.perform(put("/api/providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedProvider)))
            .andExpect(status().isOk());

        // Validate the Provider in the database
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeUpdate);
        Provider testProvider = providerList.get(providerList.size() - 1);
        assertThat(testProvider.getNom()).isEqualTo(UPDATED_NOM);

        // Validate the Provider in Elasticsearch
        verify(mockProviderSearchRepository, times(1)).save(testProvider);
    }

    @Test
    @Transactional
    public void updateNonExistingProvider() throws Exception {
        int databaseSizeBeforeUpdate = providerRepository.findAll().size();

        // Create the Provider

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProviderMockMvc.perform(put("/api/providers")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(provider)))
            .andExpect(status().isBadRequest());

        // Validate the Provider in the database
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Provider in Elasticsearch
        verify(mockProviderSearchRepository, times(0)).save(provider);
    }

    @Test
    @Transactional
    public void deleteProvider() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);

        int databaseSizeBeforeDelete = providerRepository.findAll().size();

        // Get the provider
        restProviderMockMvc.perform(delete("/api/providers/{id}", provider.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Provider> providerList = providerRepository.findAll();
        assertThat(providerList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the Provider in Elasticsearch
        verify(mockProviderSearchRepository, times(1)).deleteById(provider.getId());
    }

    @Test
    @Transactional
    public void searchProvider() throws Exception {
        // Initialize the database
        providerRepository.saveAndFlush(provider);
        when(mockProviderSearchRepository.search(queryStringQuery("id:" + provider.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(provider), PageRequest.of(0, 1), 1));
        // Search the provider
        restProviderMockMvc.perform(get("/api/_search/providers?query=id:" + provider.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(provider.getId().intValue())))
            .andExpect(jsonPath("$.[*].nom").value(hasItem(DEFAULT_NOM)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Provider.class);
        Provider provider1 = new Provider();
        provider1.setId(1L);
        Provider provider2 = new Provider();
        provider2.setId(provider1.getId());
        assertThat(provider1).isEqualTo(provider2);
        provider2.setId(2L);
        assertThat(provider1).isNotEqualTo(provider2);
        provider1.setId(null);
        assertThat(provider1).isNotEqualTo(provider2);
    }
}
