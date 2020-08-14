//The root URL for the RESTful services
//var rootURL = "http://localhost:8080/WACel";
var rootURL = "..";
/*
@T�tulo: Consultar dados de um cen�rio para gerar o Petri-Net
@Objetivo: Popular a p�gina de exibi��o de um modelo de Petri-Net do cen�rio.
@Contexto:
	- Localiza��o: camada de vis�o.
	- Pr�-condi��o: EXIBIR AS INFORMA��ES DO CEN�RIO
@Atores: sistema
@Recursos: id do cen�rio.
 */
function show_petri_net(scenario_id){
	jsonNodes = [];
	jsonEdges = [];	

	var num_nodes = 0;
	var num_arcs = 0;
	var root_node = '';

//	@Epis�dio 1: Monta requisi��o ajax e CONTROLE CONSULTA DADOS DO CEN�RIO	
	$.ajax({
		type: "GET",
		url: rootURL + "/petriNet/scenario/" + scenario_id,
		dataType: "json", // data type of response
		success: function(data){
			var has_petrinet = false;

			// get petri net
			var id = data.id;
			var name = data.name;
			var pnml = data.pnml;
			// Start file download.
			document.getElementById("dwnBtnPN").addEventListener("click", function(){
			    // Generate download of pn_<name>.pnml file with some content
			    var text = pnml;
			    var filename = "pn_" +name.split(' ').join('_') + ".pnml";
			    
			    download(filename, text);
			}, false);

			// JAX-RS serializes an empty list as null, and a 'collection of one' as an object (not an 'array of one')
			var list_nodes = data.nodes == null ? [] : (data.nodes instanceof Array ? data.nodes : [data.nodes]);
			num_nodes = list_nodes.length;
			$.each(list_nodes, function(index, node) {
				petrinet = true;
				var id_node = node.id;
				var name_node = node.name;
				var label_node = node.label;
				var type_node = node.type;

				if (type_node == "PLACE" ){
					itemNode = {}
					tmpNode = {}
					tmpNode = {
							'id': id_node,
							'name': name_node,
							'label': label_node,
							'shape': 'ellipse',
							'bg_color': '#E0FFFF'
					};

					itemNode ["data"] = tmpNode;
					jsonNodes.push(itemNode);
				}	
				if (type_node == "PLACE_WITH_TOKEN" ){ //Node with initial token

					itemNode = {}
					tmpNode = {
							'id': id_node,
							'name': name_node,
							'label': label_node,
							'shape': 'ellipse',
							'bg_color': '#B3767E'
					};

					itemNode ["data"] = tmpNode;
					jsonNodes.push(itemNode);
					//root?
					if(name_node == 'Start'){
						root_node = '#'+id_node
					}
				}
				if (type_node == "TRANSITION") {
					itemNode = {}
					tmpNode = {}

					tmpNode = {
							'id': id_node,
							'name': name_node,
							'label': name_node,
							'shape': 'rectangle',
							'bg_color': '#6272A3'
					};

					itemNode ["data"] = tmpNode;
					jsonNodes.push(itemNode);
				}

			});

			// JAX-RS serializes an empty list as null, and a 'collection of one' as an object (not an 'array of one')
			var list_arcs = data.arcs == null ? [] : (data.arcs instanceof Array ? data.arcs : [data.arcs]);
			num_arcs = list_arcs.length;
			$.each(list_arcs, function(index, arc) {
				var id_edge = arc.id;
				var id_source = arc.source.id;
				var id_target = arc.target.id;

				itemEdge = {}
				tmpEdge = {
						'id': id_source +'-'+id_target,
						'source': id_source,
						'target': id_target
				};

				itemEdge ["data"] = tmpEdge;
				jsonEdges.push(itemEdge);

			});

			//Definir layout para visualizar o P/T-N
			var cyContainer = document.getElementById('cy');
			cyContainer.style.position = 'relative';
			//cyContainer.style.top = '-20px';
			//cyContainer.style.left = '20px';
			cyContainer.style.backgroundColor = 'white';


			if(num_nodes > 10 && num_nodes < 20 ){
				//cyContainer.style.width = '500px';
				cyContainer.style.width = '100%';
				cyContainer.style.height = '800px';
			} 
			if(num_nodes >= 20 && num_nodes < 30 ){
				//cyContainer.style.width = '1000px';
				cyContainer.style.width = '100%';
				cyContainer.style.height = '1500px';
			}
			if(num_nodes >= 30 && num_nodes < 50 ){
				//cyContainer.style.width = '1500px';
				cyContainer.style.width = '100%';
				cyContainer.style.height = '1800px';
			}
			if(num_nodes >= 50 ){
				//cyContainer.style.width = '1800px';
				cyContainer.style.width = '100%';
				cyContainer.style.height = '2000px';
			}

			//LAYOUT
			alert("Gerando Place/Transition Petri-Net: "+num_nodes+"/"+num_arcs);
			var cy = cytoscape({
				container: document.getElementById('cy'),

				style: [
					{
						selector: 'node',
						css: {
							'content': 'data(label)',
							'shape': 'data(shape)',
							'text-valign': 'center',
							'text-halign': 'left',
							//'width': '10',
							//'height': '10',
							'font-size' : '10',
							'background-color': 'data(bg_color)',
						}
					},
					{
						selector: '$node > node',
						css: {
							'padding-top': '10px',
							'padding-left': '10px',
							'padding-bottom': '10px',
							'padding-right': '10px',
							'text-valign': 'top',
							'text-halign': 'center'
						}
					},
					{
						selector: 'edge',
						css: {
							'target-arrow-shape': 'triangle',
							'line-color': 'black',
							'target-arrow-color': 'black',
							'weight': '1'
						}
					},
					{
						selector: ':selected',
						css: {
							'background-color': '#F2B1BA',
							'line-color': 'black',
							'target-arrow-color': 'black',
							'source-arrow-color': 'black'
						}
					}
					],

					elements: {
						nodes: jsonNodes,

						edges: jsonEdges
					},

					layout: {
						//name: 'circle',
						//name: 'breadthfirst',
						//name: 'grid',
						name: 'breadthfirst',
						//name: 'cose',

						fit: true, // whether to fit the viewport to the graph
						directed: true, // whether the tree is directed downwards (or edges can point in any direction if false)
						padding: 5, // padding on fit
						circle: false, // put depths in concentric circles if true, put depths top down if false
						spacingFactor: 1.75, // positive spacing factor, larger => more space between nodes (N.B. n/a if causes overlap)
						boundingBox: undefined, // constrain layout bounds; { x1, y1, x2, y2 } or { x1, y1, w, h }
						avoidOverlap: true, // prevents node overlap, may overflow boundingBox if not enough space
						//roots: undefined, // the roots of the trees
						roots: root_node,
						//roots: ('#144_5','#144_7'),
						//roots: elements('node[name = "Start"]'), // but this does
						maximalAdjustments: 0, // how many times to try to position the nodes in a maximal way (i.e. no backtracking)
						animate: false, // whether to transition the node positions
						animationDuration: 500, // duration of animation in ms if enabled
						ready: undefined, // callback on layoutready
						stop: undefined // callback on layoutstop
					}
			});

			// just use the regular qtip api but on cy elements
			cy.elements().qtip({
				content: function(){ if (this.data('shape') == 'rectangle') return this.data('name');},
				position: {
					my: 'top center',
					at: 'bottom center'
				},
				style: {
					classes: 'qtip-bootstrap',
					tip: {
						width: 16,
						height: 8
					}
				}
			});


		},

		error: function(msg){
			alert( "Erro no ajax ao gerar a petri-net");
		}
	});
}

/*
@T�tulo: Consultar dados de um cen�rio para gerar o Petri-Net do cen'ario principal e seus relacionamentos-nao-sequenciais
@Objetivo: Popular a p�gina de exibi��o de um modelo de Petri-Net do cen�rio.
@Contexto:
	- Localiza��o: camada de vis�o.
	- Pr�-condi��o: EXIBIR AS INFORMA��ES DO CEN�RIO
@Atores: sistema
@Recursos: id do cen�rio.
 */
function show_integrated_petri_net(project_id, scenario_id){
	jsonNodes = [];
	jsonEdges = [];	

	jsonGroupNodes = []; //Nodes that group related nodes

	var num_nodes = 0;
	var num_arcs = 0;
	var root_nodes = '';

//	@Epis�dio 1: Monta requisi��o ajax e CONTROLE CONSULTA DADOS DO CEN�RIO	
	$.ajax({
		type: "GET",
		url: rootURL + "/petriNet/integrated/project/"+project_id+"/scenario/" + scenario_id,
		dataType: "json", // data type of response
		success: function(data){
			var has_petrinet = false;

			// get petri net
			var id = data.id;
			var name = data.name;
			
			var pnml = data.pnml;
			// Start file download.
			document.getElementById("dwnBtnPN").addEventListener("click", function(){
			    // Generate download of integrated_pn_<name>.pnml file with some content
			    var text = pnml;
			    var filename = "integrated_pn_" +name.split(' ').join('_') + ".pnml";
			    
			    download(filename, text);
			}, false);
			
			// JAX-RS serializes an empty list as null, and a 'collection of one' as an object (not an 'array of one')
			var list_nodes = data.nodes == null ? [] : (data.nodes instanceof Array ? data.nodes : [data.nodes]);
			num_nodes = list_nodes.length;
			$.each(list_nodes, function(index, node) {
				petrinet = true;
				var id_node = node.id;
				var name_node = node.name;
				var label_node = node.label;
				var type_node = node.type;

				var group_name = node.groupName;

				if (group_name != null || group_name != '') {
					//find existing node-group
					itemNode = {}
					tmpNode = {
							'id': group_name,
							'name': group_name,
							'label': group_name,
							'shape': 'ellipse',
							'bg_color': '#6272A3'					
					};
					itemNode ["data"] = tmpNode;
					if(!jsonGroupNodes.includes(itemNode)) {
						jsonGroupNodes.push(itemNode);
					}
				}


				if (type_node == "PLACE" ){
					itemNode = {}
					tmpNode = {}

					if (group_name != null || group_name != '') {
						tmpNode = {
								'id': id_node,
								'name': name_node,
								'label': label_node,
								'shape': 'ellipse',
								'bg_color': '#E0FFFF',
								'parent': group_name
						};
					} else{
						tmpNode = {
								'id': id_node,
								'name': name_node,
								'label': label_node,
								'shape': 'ellipse',
								'bg_color': '#E0FFFF'
						};
					}
					itemNode ["data"] = tmpNode;
					jsonNodes.push(itemNode);
				}	
				if (type_node == "PLACE_WITH_TOKEN" ){ //Node with initial token
					itemNode = {}
					tmpNode = {}

					if (group_name != null || group_name != '') {
						tmpNode = {
								'id': id_node,
								'name': name_node,
								'label': label_node,
								'shape': 'ellipse',
								'bg_color': '#B3767E',
								'parent': group_name
						};
					} else{
						tmpNode = {
								'id': id_node,
								'name': name_node,
								'label': label_node,
								'shape': 'ellipse',
								'bg_color': '#B3767E'
						};
					}

					itemNode ["data"] = tmpNode;
					jsonNodes.push(itemNode);
					//root?
					if(name_node == 'Start'){
						if (root_nodes == ''){
							root_nodes = '#'+id_node
						} else {
							root_nodes = root_nodes + ',#'+id_node
						}
					}
					
				}
				if (type_node == "TRANSITION") {
					itemNode = {}
					tmpNode = {}

					if (group_name != null || group_name != '') {
						tmpNode = {
								'id': id_node,
								'name': name_node,
								'label': name_node,
								'shape': 'rectangle',
								'bg_color': '#6272A3',
								'parent': group_name
						};
					} else {
						tmpNode = {
								'id': id_node,
								'name': name_node,
								'label': name_node,
								'shape': 'rectangle',
								'bg_color': '#6272A3'
						};
					}
					itemNode ["data"] = tmpNode;
					jsonNodes.push(itemNode);
				}

			});
			//Concatenate nodes and group-nodes
			jsonNodes = jsonGroupNodes.concat(jsonNodes);
			
			// JAX-RS serializes an empty list as null, and a 'collection of one' as an object (not an 'array of one')
			var list_arcs = data.arcs == null ? [] : (data.arcs instanceof Array ? data.arcs : [data.arcs]);
			num_arcs = list_arcs.length;
			$.each(list_arcs, function(index, arc) {
				var id_edge = arc.id;
				var id_source = arc.source.id;
				var id_target = arc.target.id;

				itemEdge = {}
				tmpEdge = {
						'id': id_source +'-'+id_target,
						'source': id_source,
						'target': id_target
				};

				itemEdge ["data"] = tmpEdge;
				jsonEdges.push(itemEdge);

			});

			//Definir layout para visualizar o P/T-N
			var cyContainer = document.getElementById('cy');
			cyContainer.style.position = 'relative';
			//cyContainer.style.top = '-20px';
			//cyContainer.style.left = '20px';
			cyContainer.style.backgroundColor = 'white';
			
			
			if(num_nodes > 10 && num_nodes < 20 ){
				//cyContainer.style.width = '500px';
				cyContainer.style.width = '100%';
				cyContainer.style.height = '800px';
			} 
			if(num_nodes >= 20 && num_nodes < 30 ){
				//cyContainer.style.width = '1000px';
				cyContainer.style.width = '100%';
				cyContainer.style.height = '1500px';
			}
			if(num_nodes >= 30 && num_nodes < 50 ){
				//cyContainer.style.width = '1500px';
				cyContainer.style.width = '100%';
				cyContainer.style.height = '1800px';
			}
			if(num_nodes >= 50 ){
				//cyContainer.style.width = '1800px';
				cyContainer.style.width = '100%';
				cyContainer.style.height = '2000px';
			}
			
			//LAYOUT
			alert("Gerando Place/Transition Petri-Net: "+num_nodes+"/"+num_arcs);
			var cy = cytoscape({
				container: document.getElementById('cy'),

				style: [
					{
					selector: 'node',
						css: {
						'content': 'data(label)',
						'shape': 'data(shape)',
						'text-valign': 'center',
						'text-halign': 'left',
						//'width': '10',
						//'height': '10',
						'font-size' : '10',
						'background-color': 'data(bg_color)',
						}
					},
					{
					selector: '$node > node',
						css: {
						'padding-top': '10px',
						'padding-left': '10px',
						'padding-bottom': '10px',
						'padding-right': '10px',
						'text-valign': 'top',
						'text-halign': 'center'
						}
					},
					{
					selector: 'edge',
						css: {
						'target-arrow-shape': 'triangle',
						'line-color': 'black',
						'target-arrow-color': 'black',
						'weight': '1'
						}
					},
					{
					selector: ':selected',
						css: {
						'background-color': '#F2B1BA',
						'line-color': 'black',
						'target-arrow-color': 'black',
						'source-arrow-color': 'black'
						}
					}
				],

				elements: {
					nodes: jsonNodes,

					edges: jsonEdges
				},

				layout: {
					//name: 'circle',
					//name: 'breadthfirst',
					//name: 'grid',
					name: 'breadthfirst',
					//name: 'cose',
											
					fit: true, // whether to fit the viewport to the graph
					directed: true, // whether the tree is directed downwards (or edges can point in any direction if false)
					padding: 5, // padding on fit
					circle: false, // put depths in concentric circles if true, put depths top down if false
					spacingFactor: 1.75, // positive spacing factor, larger => more space between nodes (N.B. n/a if causes overlap)
					boundingBox: undefined, // constrain layout bounds; { x1, y1, x2, y2 } or { x1, y1, w, h }
					avoidOverlap: true, // prevents node overlap, may overflow boundingBox if not enough space
					//roots: undefined, // the roots of the trees
					roots: root_nodes, // but this does
					maximalAdjustments: 0, // how many times to try to position the nodes in a maximal way (i.e. no backtracking)
					animate: false, // whether to transition the node positions
					animationDuration: 500, // duration of animation in ms if enabled
					ready: undefined, // callback on layoutready
					stop: undefined // callback on layoutstop
				}
			});
			
			// just use the regular qtip api but on cy elements
			cy.elements().qtip({
				content: function(){ if (this.data('shape') == 'rectangle') return this.data('name');},
				//content: function(){ return this.data('name');},
				position: {
					my: 'top center',
					at: 'bottom center'
				},
				style: {
					classes: 'qtip-bootstrap',
					tip: {
						width: 16,
						height: 8
					}
				}
			});

		},

		error: function(msg){
			alert( "Erro no ajax ao gerar a petri-net: ");
		}
	});
}

/*
 * HTML5
 * https://ourcodeworld.com/articles/read/189/how-to-create-a-file-and-generate-a-download-with-javascript-in-the-browser-without-a-server
 */
function download(filename, text) {
    var element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);

    element.style.display = 'none';
    document.body.appendChild(element);

    element.click();

    document.body.removeChild(element);
}