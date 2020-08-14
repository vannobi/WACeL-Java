// The root URL for the RESTful services
//var rootURL = "http://localhost:8080/WACel";
var rootURL = "..";


/*
@Título: Montar menu de cenários e léxicos
@Objetivo: Exibir no menu no lado esquerdo da página principal os léxicos e cenários de um projeto.
@Contexto:
	Localização: camada de visão.
	Pré-condição: SELECIONAR PROJETO DO MENU DE PROJETOS.
@Atores: sistema.
@Recursos: id do projeto, título e id dos cenários do projeto e nome e id dos símbolos do léxico do projeto
*/
function create_menu_scenarios_lexicons (project_id) {
	
	if (project_id != "-1")	{
		// @Episódio 1: Montagem da requisição ajax para cenários do projeto
			$.ajax({
				type: "GET",
				url: rootURL + "/scenario/project/" + project_id + "/scenarios",
				dataType: "json", // data type of response
				success: function(data){
					var has_scenarios = false;
					
					// JAX-RS serializes an empty list as null, and a 'collection of one' as an object (not an 'array of one')
					var list = data == null ? [] : (data instanceof Array ? data : [data]);
		// @Episódio 2: Inclui no menu lateral os cenários obtidos do xml.
					$("#nav_scenarios_menu").empty();
					$('#nav_scenarios_menu div').remove();
											
					$.each(list, function(index, scenario) {
						has_scenarios = true;
						var title = scenario.title;
						var scenario_id = scenario.id;
						
		// @Episódio 3: Monta o link para carregar as informações do cenário no div de conteúdo e o inclui no menu de cenários.				
						var obj_a = $('<a>').attr('href',rootURL+'/scenario/show-scenario-' + scenario_id).attr('class','list-group-item list-group-item-action list-group-item-light border border-info rounded').html(title);  
						$('#nav_scenarios_menu').append(obj_a);
												
					});
				
							
		// @Episódio 4: Se não houver cenários no projeto, exibe uma mensagem no menu lateral informando o usuário.			
					if (!has_scenarios){
						$('#nav_scenarios_menu').append($('<div>').attr('class','alert alert-danger').html("N&atilde;o h&aacute; cen&aacute;rios cadastrados neste projeto."));
					}
		
				},
				
		//@Exceção: Se houver algum erro durante a execução do ajax, informa ao usuário que ocorreu erro na montagem do menu lateral
				error: function(msg){
					alert( "Erro no ajax do menu lateral de cenários: ");
				}
			});
			
		// @Episódio 5: Montagem da requisição ajax para símbolos do léxico do projeto	
			$.ajax({
				type: "GET",
				url: rootURL + "/lexicon/project/" + project_id + "/lexicons",
				dataType: "json", // data type of response
				success: function(data){
					var has_lexicons = false;
										
					// JAX-RS serializes an empty list as null, and a 'collection of one' as an object (not an 'array of one')
					var list = data == null ? [] : (data instanceof Array ? data : [data]);
		// @Episódio 6: Inclui no menu lateral os lexicos obtidos do xml.
					$("#nav_lexicons_menu").empty();
					$('#nav_lexicons_menu div').remove();
					
					$.each(list, function(index, lexicon) {
						has_lexicons = true;
						var name = lexicon.name;
						var lexicon_id = lexicon.id;
						
		// @Episódio 7: Monta o link para carregar as informações do léxico no div de conteúdo e o inclui no menu de simbolos do léxico.				
						var obj_a = $('<a>').attr('href',rootURL+'/lexicon/show-lexicon-' + lexicon_id).attr('class','list-group-item list-group-item-action list-group-item-light  border border-info rounded').html(name);  
						$('#nav_lexicons_menu').append(obj_a);
												
					});
				
		// @Episódio 7: Se não houver léxicos no projeto, exibe uma mensagem no menu lateral informando o usuário.				
		
					if (!has_lexicons){
						$('#nav_lexicons_menu').append($('<div>').attr('class','alert alert-danger').html("N&atilde;o h&aacute; s&iacute;mbolos do l&eacute;xico cadastrados neste projeto."));
					}
		
				},
				
		//@Exceção: Se houver algum erro durante a execução do ajax, informa ao usuário que ocorreu erro na montagem do menu lateral
				error: function(msg){
					alert( "Erro no ajax do menu lateral de Léxicos: ");
				}
			});
			// @Episódio 8: Mostrar o Menu de Léxico e Cenário.	
			//Show Lexicon and Scenario Menu
			$('#li_nav_lexicon').show();
			$('#li_nav_scenario').show();
						
	}
	
	else {
		//Hide Lexicon and Scenario Menu
		$('#li_nav_lexicon').hide();
		$('#li_nav_scenario').hide();
		
		//Main content
		$("#div_main_content").empty();
		$("#div_main_content div").remove();
		
		$("#nav_scenarios_menu").empty();
		$('#nav_scenarios_menu div').remove();
		$("#nav_lexicons_menu").empty();
		$('#nav_lexicons_menu div').remove();
		
		$('#nav_scenarios_menu').append($('<div>').attr('class','alert alert-warning').html('<fmt:message key="project.select.option"/>'));
		$('#nav_lexicons_menu').append($('<div>').attr('class','alert alert-warning').html("<fmt:message key='project.select.option'/>"));
	}

}
