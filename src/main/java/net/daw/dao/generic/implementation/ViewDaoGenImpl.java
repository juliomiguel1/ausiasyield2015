/*
 * Copyright (C) July 2014 Rafael Aznar
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.daw.dao.generic.implementation;

import net.daw.dao.publicinterface.ViewDaoInterface;
import net.daw.dao.publicinterface.MetaDaoInterface;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import net.daw.bean.generic.implementation.BeanGenImpl;
import net.daw.helper.statics.AppConfigurationHelper;
import net.daw.helper.statics.ExceptionBooster;
import net.daw.helper.statics.FilterBeanHelper;

public class ViewDaoGenImpl<BEAN_CLASS> extends MetaDaoGenImpl<BEAN_CLASS> implements ViewDaoInterface<BEAN_CLASS>, MetaDaoInterface<BEAN_CLASS> {

    public ViewDaoGenImpl(Connection pooledConnection) throws Exception {
        super(pooledConnection);
    }

    @Override
    public int getPages(int intRegsPerPag, ArrayList<FilterBeanHelper> hmFilter) throws Exception {
        int pages = 0;
        try {
            pages = oMysql.getPages(strView, intRegsPerPag, hmFilter);
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":getPages ERROR: " + ex.getMessage()));
        }
        return pages;
    }

    @Override
    public int getCount(ArrayList<FilterBeanHelper> hmFilter) throws Exception {
        int pages = 0;
        try {
            pages = oMysql.getCount(strView, hmFilter);
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":getCount ERROR: " + ex.getMessage()));
        }
        return pages;
    }

    @Override
    public ArrayList<BEAN_CLASS> getPage(int intRegsPerPag, int intPage, ArrayList<FilterBeanHelper> hmFilter, HashMap<String, String> hmOrder) throws Exception {
        Class<BEAN_CLASS> tipo = (Class<BEAN_CLASS>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        Method metodo_setId = tipo.getMethod("setId", Integer.class);
        ArrayList<Integer> arrId;
        ArrayList<BEAN_CLASS> arrCliente = new ArrayList<>();
        try {
            arrId = oMysql.getPage(strView, intRegsPerPag, intPage, hmFilter, hmOrder);
            Iterator<Integer> iterador = arrId.listIterator();
            while (iterador.hasNext()) {
                Object oBean = Class.forName(tipo.getName()).newInstance();
                metodo_setId.invoke(oBean, iterador.next());
                arrCliente.add(this.get((BEAN_CLASS) oBean, AppConfigurationHelper.getJsonDepth()));
            }
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":getPage ERROR: " + ex.getMessage()));
        }
        return arrCliente;

    }

    private void parseValue(BEAN_CLASS oBean, Method method, String strTipoParamMetodoSet, String strValor) throws Exception {
        try {
            switch (strTipoParamMetodoSet) {
                case "java.lang.Double":
                    method.invoke(oBean, Double.parseDouble(strValor));
                    break;
                case "java.lang.Integer":
                    method.invoke(oBean, Integer.parseInt(strValor));
                    break;
                case "java.lang.Boolean":
                    if (Integer.parseInt(strValor) == 1) {
                        method.invoke(oBean, true);
                    } else {
                        method.invoke(oBean, false);
                    }
                    break;
                case "java.util.Date":
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    method.invoke(oBean, format.parse(strValor));
                    break;
                default:
                    method.invoke(oBean, strValor);
                    break;
            }
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":parseValue ERROR: " + ex.getMessage()));
        }
    }

    private BEAN_CLASS fillForeign(BEAN_CLASS oBean, Class<BEAN_CLASS> tipo) throws Exception {
        try {
            for (Method method : tipo.getMethods()) {
                if (method.getName().length() >= 5) { //los campos como minimo han de tener dos caracteres + el get o el set = 5 caracteres
                    if (!method.getName().substring(3).equalsIgnoreCase("id")) {
                        if (method.getName().substring(0, 3).equalsIgnoreCase("set")) {
                            final Class<?> classTipoParamMetodoSet = method.getParameterTypes()[0];
                            if (method.getName().length() >= 5) {
                                if (method.getName().substring(3).length() >= 4) {
                                    if (method.getName().substring(3).toLowerCase(Locale.ENGLISH).substring(0, 4).equalsIgnoreCase("obj_")) {
                                        //prueba: method.getName().substring(method.getName().indexOf("_")+1,method.getName().lastIndexOf("_")))
                                        //ojo: en los pojos, los id_ deben preceder a los obj_ del mismo objeto siempre!
                                        //only two _ allowed in foreign keys
                                        String strAjena, strTabla = null;

                                        if (!(method.getName().indexOf("_") == (method.getName().lastIndexOf("_")))) {
                                            strTabla = method.getName().substring(method.getName().indexOf("_") + 1, method.getName().lastIndexOf("_")).toLowerCase(Locale.ENGLISH);
                                            strAjena = method.getName().substring(3).toLowerCase(Locale.ENGLISH).substring(4);
                                        } else {
                                            strAjena = method.getName().substring(3).toLowerCase(Locale.ENGLISH).substring(4);
                                            strTabla = strAjena;
                                        }
                                        Method metodo_getId_Ajena = tipo.getMethod("getId_" + strAjena);
                                        strTabla = strTabla.substring(0, 1).toUpperCase(Locale.ENGLISH) + strTabla.substring(1);
                                //GenericDaoImplementation oAjenaDao = (GenericDaoImplementation) Class.forName("net.daw.dao." + strAjena + "Dao").newInstance();

                                        //rafa: aqui intentamos crear el DAO de la clave ajena generico-específico y si no espcífico
                                        //pte porque da error
                                        Constructor c;
                                        try {
                                            c = Class.forName("net.daw.dao.generic.specific.implementation." + strTabla + "DaoGenSpImpl").getConstructor(String.class, Connection.class);
                                        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
                                            //aqui da el error--> pte d eestudiar
                                            c = Class.forName("net.daw.dao.specific.implementation." + strTabla + "DaoSpcImpl").getConstructor(String.class, Connection.class);
                                        }
                                        //------------------------------------

                                        TableDaoGenImpl oAjenaDao = (TableDaoGenImpl) c.newInstance(strTabla, connection);

                                        BeanGenImpl oAjenaBean = (BeanGenImpl) Class.forName("net.daw.bean.generic.specific.implementation." + strTabla + "BeanGenSpImpl").newInstance();
                                        int intIdAjena = (Integer) metodo_getId_Ajena.invoke(oBean);
                                        oAjenaBean.setId(intIdAjena);
                                        oAjenaBean = (BeanGenImpl) oAjenaDao.get(oAjenaBean, AppConfigurationHelper.getJsonDepth());
                                        //String strDescription = oAjenaDao.getDescription((Integer) metodo_getId_Ajena.invoke(oBean));
                                        method.invoke(oBean, oAjenaBean);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":fillForeign ERROR: " + ex.getMessage()));
        }
        return oBean;
    }

    private BEAN_CLASS fill(BEAN_CLASS oBean, Class<BEAN_CLASS> tipo, Method metodo_getId) throws Exception {
        try {
            for (Method method : tipo.getMethods()) {
                if (!method.getName().substring(3).equalsIgnoreCase("id")) {
                    if (method.getName().substring(0, 3).equalsIgnoreCase("set")) {
                        final Class<?> classTipoParamMetodoSet = method.getParameterTypes()[0];
                        if (method.getName().length() >= 5) {
                            if (method.getName().substring(3).length() >= 4) {
                                if (!method.getName().substring(3).toLowerCase(Locale.ENGLISH).substring(0, 4).equalsIgnoreCase("obj_")) {
                                    String strValor = oMysql.getOne(strView, method.getName().substring(3).toLowerCase(Locale.ENGLISH), (Integer) metodo_getId.invoke(oBean));
                                    if (strValor != null) {
                                        parseValue(oBean, method, classTipoParamMetodoSet.getName(), strValor);
                                    }
                                }
                            } else {
                                String strValor = oMysql.getOne(strView, method.getName().substring(3).toLowerCase(Locale.ENGLISH), (Integer) metodo_getId.invoke(oBean));
                                if (strValor != null) {
                                    parseValue(oBean, method, classTipoParamMetodoSet.getName(), strValor);
                                }
                            }
                        } else {
                            String strValor = oMysql.getOne(strView, method.getName().substring(3).toLowerCase(Locale.ENGLISH), (Integer) metodo_getId.invoke(oBean));
                            if (strValor != null) {
                                parseValue(oBean, method, classTipoParamMetodoSet.getName(), strValor);
                            }
                        }

                    }
                }
            }

        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":fill ERROR: " + ex.getMessage()));
        }
        return oBean;
    }

    @Override
    public BEAN_CLASS get(BEAN_CLASS oBean, Integer expand) throws Exception {
        try {
            Class<BEAN_CLASS> tipo = (Class<BEAN_CLASS>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            Method metodo_getId = tipo.getMethod("getId");
            Method metodo_setId = tipo.getMethod("setId", Integer.class);
            if ((Integer) metodo_getId.invoke(oBean) > 0) {
                try {
                    if (!oMysql.existsOne(strView, (Integer) metodo_getId.invoke(oBean))) {
                        metodo_setId.invoke(oBean, 0);
                    } else {
                        oBean = fill(oBean, tipo, metodo_getId);
                        expand--;
                        if (expand > 0) {
                            oBean = fillForeign(oBean, tipo);
                        }

                    }
                } catch (Exception e) {
                    throw new Exception("GenericViewDaoImpl.get: Error: " + e.getMessage());
                }
            } else {
                metodo_setId.invoke(oBean, 0);
            }
        } catch (Exception ex) {
            ExceptionBooster.boost(new Exception(this.getClass().getName() + ":get ERROR: " + ex.getMessage()));
        }
        return oBean;

    }

}
