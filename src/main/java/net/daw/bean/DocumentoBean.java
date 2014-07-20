/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.daw.bean;

import com.google.gson.annotations.Expose;
import java.util.Date;

/**
 *
 * @author Alvaro
 */
public class DocumentoBean extends GenericBeanImplementation implements GenericBeanInterface {

    @Expose
    private String titulo = "";
    //private String presentacion = "";
    @Expose
    private String contenido = "";
    @Expose
    private Date alta = new Date();
    @Expose
    private Date cambio = new Date();
    @Expose
    private Integer hits = 0;
    @Expose(serialize = false)
    private Integer id_usuario = 0; //importante inicializar a 0 las claves ajenas
    @Expose(deserialize = false)
    private UsuarioBean obj_usuario = null;
    @Expose(serialize = false)
    private Integer id_tipodocumento = 0; //importante inicializar a 0 las claves ajenas
    @Expose(deserialize = false)
    private TipodocumentoBean obj_tipodocumento = null;
    @Expose
    private String etiquetas = "";
    @Expose
    private Boolean publicado = false;
    @Expose
    private Boolean portada = false;
    @Expose
    private Boolean destacado = false;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) throws Exception {
        this.contenido = contenido;
    }

//     public String getPresentacion() {
//        return presentacion;
//    }
//
//    public void setPresentacion(String contenidoParseado) {
//        this.presentacion = contenidoParseado;
//    }
    public Date getAlta() {
        return alta;
    }

    public void setAlta(Date fecha) {
        this.alta = fecha;
    }

    public Date getCambio() {
        return cambio;
    }

    public void setCambio(Date fecha) {
        this.cambio = fecha;
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public Integer getId_tipodocumento() {
        return id_tipodocumento;
    }

    public void setId_tipodocumento(Integer id_tipodocumento) {
        this.id_tipodocumento = id_tipodocumento;
    }

    public String getEtiquetas() {
        return etiquetas;
    }

    public void setEtiquetas(String etiquetas) {
        this.etiquetas = etiquetas;
    }

    public Boolean getPublicado() {
        return publicado;
    }

    public void setPublicado(Boolean publicado) {
        this.publicado = publicado;
    }

    public Boolean getPortada() {
        return portada;
    }

    public void setPortada(Boolean portada) {
        this.portada = portada;
    }

    public Boolean getDestacado() {
        return destacado;
    }

    public void setDestacado(Boolean destacado) {
        this.destacado = destacado;
    }

    public UsuarioBean getObj_usuario() {
        return obj_usuario;
    }

    public void setObj_usuario(UsuarioBean obj_usuario) {
        this.obj_usuario = obj_usuario;
    }

    public TipodocumentoBean getObj_tipodocumento() {
        return obj_tipodocumento;
    }

    public void setObj_tipodocumento(TipodocumentoBean obj_tipodocumento) {
        this.obj_tipodocumento = obj_tipodocumento;
    }

}
