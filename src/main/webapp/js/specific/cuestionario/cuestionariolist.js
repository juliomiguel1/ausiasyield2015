/* 
 * Copyright (c) 2015 by Rafael Angel Aznar Aparici (rafaaznar at gmail dot com)
 * 
 * openAUSIAS: The stunning micro-library that helps you to develop easily 
 * AJAX web applications by using Java and jQuery
 * openAUSIAS is distributed under the MIT License (MIT)
 * Sources at https://github.com/rafaelaznar/openAUSIAS
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

var cuestionarioList = function () {


};

cuestionarioList.prototype = new listModule();

// check
cuestionarioList.prototype.loadThButtons = function (meta, strClase, UrlFromParamsWithoutOrder) {
    return button.getTableHeaderButtons(meta.Name, strClase, 'list', UrlFromParamsWithoutOrder);
}

//check
cuestionarioList.prototype.loadButtons = function (rowValues, strOb) {
    var botonera = "";
    botonera += button.getTableToobarButton(strOb, 'view', rowValues[0].data, 'glyphicon-eye-open');
    botonera += button.getTableToobarButton(strOb, 'edit', rowValues[0].data, 'glyphicon-pencil');
    botonera += button.getTableToobarButton(strOb, 'remove', rowValues[0].data, 'glyphicon-remove');
    return button.getToolbarBar(botonera);
};



cuestionarioList.prototype.getHeaderPageTableFunc = function (jsonMeta, strOb, UrlFromParamsWithoutOrder, visibles, acciones) {
    thisObject = this;
    acciones = typeof (acciones) != 'undefined' ? acciones : true;
    
     arr_meta_data_tableHeader_filtered = _.filter(jsonMeta, function(oItem){
        if (oItem.ShortName=="Título" ) {
            return true;
        } else {
            return false;
        }
    } );


    
    arr_meta_data_tableHeader = _.map(arr_meta_data_tableHeader_filtered, function (oMeta, key) {
        
        if (oMeta.Name == "titulo") {
            return '<th class="col-md-1">'
                    + oMeta.ShortName
                    + '<br />'
                    + thisObject.loadThButtons(oMeta, strOb, UrlFromParamsWithoutOrder)
                    + '</th>';
        } else if (oMeta.Name == "titulo") {
            return '<th>'
                    + oMeta.ShortName
                    + '<br />'
                    + thisObject.loadThButtons(oMeta, strOb, UrlFromParamsWithoutOrder)
                    + '</th>';
        } 
    });
    //visibles
    if (visibles) {
        arr_meta_data_tableHeader_visibles = arr_meta_data_tableHeader.slice(0, parseInt(visibles));
    } else {
        arr_meta_data_tableHeader_visibles = arr_meta_data_tableHeader;
    }
    if (acciones) {
        arr_meta_data_tableHeader_visibles_acciones = arr_meta_data_tableHeader_visibles.concat(['<th class="col-md-1">Acciones </th>']);
    } else {
        arr_meta_data_tableHeader_visibles_acciones = arr_meta_data_tableHeader_visibles;
    }
    return '<tr>' + arr_meta_data_tableHeader_visibles_acciones.join('') + '</tr>';
}
cuestionarioList.prototype.getBodyPageTableFunc = function (meta, page, printPrincipal, tdButtons_function, trPopup_function, visibles) {
    //thisObject.jsonData.message.page.list: es un array de objetos. Cada objeto contiene una fila de la tabla de la petición
    //thisObject.jsonData.message.meta; es un array de objetos. Every object contains metadata from every object to print in every row
    var matrix_meta_data = _.map(page, function (oRow, keyRow) {
        return _.map(meta, function (oMeta, keyMeta) {
            return  {meta: oMeta, data: oRow[oMeta.Name]};
        });
    });
    //Filtra los campos del array de objetos recogiendo los que son necesarios en nuestro caso
    matrix_meta_data_filtered = _.map(matrix_meta_data,function(oFilter){
        return _.pick(oFilter,0);
    });
    //is an array (rpp) of arrays (rows) of objects
    //every object contains the data and its metadata
    var arr_meta_data_table_buttons = _.map(matrix_meta_data_filtered, function (value, key) {
        return (_.map(matrix_meta_data_filtered[key], function (value2, key2) {
//            return  
            
         if(value2.meta.ShortName == "Título"){   
                return  '<td class="col-md-11">'
                        + printPrincipal(value2)
                        +'</td>'
            }else{
                
                return '<td class="col-md-1">' 
                       + printPrincipal(value2) 
                       + '</td>';
            }
        
        })
                )
                .slice(0, parseInt(visibles))
                .concat(['<td>' + tdButtons_function(value, strOb) + '</td>']);
    });
    //is an array (rpp) of arrays (rows) of strings
    //every string contains the data of the table cell
    //there's an additional row to contain the buttons for the operations
    var arr_meta_data_table_buttons_reduced = _.map(arr_meta_data_table_buttons, function (value, key) {
        return _.reduce(value, function (memo, num) {
            return memo + num;
        });
    });
    //is an array (rpp) of strings 
    //where every string is a ...
    var str_meta_data_table_buttons_reduced_reduced = _.reduce(arr_meta_data_table_buttons_reduced, function (memo, num) {
        return memo + '<td>' + num + '</td>';
    });
    //is a string that contains the table body
    return str_meta_data_table_buttons_reduced_reduced;
}
