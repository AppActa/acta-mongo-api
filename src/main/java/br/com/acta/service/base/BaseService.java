package br.com.acta.service.base;

import br.com.acta.common.config.security.UsuarioAutenticado;
import br.com.acta.document.base.BaseDocument;
import br.com.acta.dto.mapper.base.BaseMapper;
import br.com.acta.repository.base.BaseRepository;
import br.com.acta.service.AuthService;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class BaseService<REQ, RESP, ENT extends BaseDocument> implements BaseCRUD<REQ, RESP> {
    protected final BaseRepository<ENT> repo;
    protected final BaseMapper<REQ, RESP, ENT> mapper;
    protected final AuthService authService;

    protected BaseService(BaseRepository<ENT> repo, BaseMapper<REQ, RESP, ENT> mapper, AuthService authService) {
        this.repo = repo;
        this.mapper = mapper;
        this.authService = authService;
    }

    public UsuarioAutenticado atual() {
        return authService.atual();
    }

    protected abstract ENT getEntity(UUID id);

    @Override
    @Transactional(readOnly = true)
    public RESP buscar(UUID id) {
        ENT ent = getEntity(id);
        return mapper.toResponse(ent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RESP> buscar() {
        List<ENT> entList = repo.findAll();
        return mapper.toResponseList(entList);
    }

    @Override
    @Transactional
    public RESP inserir(REQ dto) {
        ENT ent = mapper.toEntity(dto);
        ENT salvo = repo.save(ent);
        return mapper.toResponse(salvo);
    }

    @Transactional
    @Override
    abstract public RESP patch(UUID id, Map<String, Object> campos);

    @Override
    @Transactional
    public void excluir(UUID id) {
        ENT ent = getEntity(id);
        repo.delete(ent);
    }
}
