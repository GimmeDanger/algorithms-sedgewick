#include <iostream>

#include "graph.h"
#include "depth_first_search.hpp"
#include "breadth_first_search.hpp"
#include "connected_components.hpp"
#include "bipartite.hpp"

using namespace std;

int main ()
{
	graph G (5);
	G.add_edge (0, 1);
	G.add_edge (0, 2);
	G.add_edge (3, 4);

	cout << "DFS Testing: " << endl;
	
	depth_first_search dfs_1_r (G, 1, true /*use_recursive_dfs*/);
	cout << dfs_1_r.connected_vertexes () << endl;
	for (int w = 0; w < G.get_vertexex_num (); w++)
		{
			cout << dfs_1_r.has_path_to (w) << " ";
		}
	cout << endl;

	depth_first_search dfs_1_nr (G, 1, false /*use_recursive_dfs*/);
	cout << dfs_1_nr.connected_vertexes () << endl;
	for (int w = 0; w < G.get_vertexex_num (); w++)
		{
			cout << dfs_1_nr.has_path_to (w) << " ";
		}
	cout << endl;

	depth_first_search dfs_3_r (G, 3, true /*use_recursive_dfs*/);
	cout << dfs_3_r.connected_vertexes () << endl;
	for (int w = 0; w < G.get_vertexex_num (); w++)
		{
			cout << dfs_3_r.has_path_to (w) << " ";
		}
	cout << endl;

	depth_first_search dfs_3_nr (G, 3, false /*use_recursive_dfs*/);
	cout << dfs_3_nr.connected_vertexes () << endl;
	for (int w = 0; w < G.get_vertexex_num (); w++)
		{
			cout << dfs_3_nr.has_path_to (w) << " ";
		}
	cout << endl << endl;

	cout << "BFS Paths Testing: " << endl;

	graph GG (6);	

	GG.add_edge (0, 1);
	GG.add_edge (0, 5);
	GG.add_edge (0, 2);
	GG.add_edge (1, 2);
	GG.add_edge (2, 4);
	GG.add_edge (2, 3);
	GG.add_edge (3, 5);
	GG.add_edge (3, 4);

	breadth_first_search bfs_paths (GG, 0);
	for (int w = 0; w < GG.get_vertexex_num (); w++)
		{
			cout << "rho(0, " << w << ") = " << bfs_paths.distance_to (w) << endl;
		}
	cout << endl;

	cout << "CC Testing: " << endl;

	graph GGG (9);

	GGG.add_edge (0, 1);
	GGG.add_edge (0, 2);
	GGG.add_edge (1, 2);
	GGG.add_edge (2, 3);
	GGG.add_edge (4, 5);
	GGG.add_edge (5, 6);
	GGG.add_edge (4, 6);
	GGG.add_edge (7, 8);

	connected_components cc (GGG);
	cout << "Graph contains " << cc.get_connected_components_num () << " connected components." << endl;
	for (int v = 0; v < GGG.get_vertexex_num (); v++)
		{
			auto cc_v = cc.get_connected_component (v);
			cout << "Vertex " << v << " belongs to cc " << cc.get_connected_component_id (v) << endl;
			for (auto w : cc_v)
				cout << w << " ";
			cout << endl;
		}
	cout << endl;

	cout << "bipartite Testing: " << endl;

	graph GGGG_1 (8);

	GGGG_1.add_edge (0, 1);
    GGGG_1.add_edge (0, 2);
	GGGG_1.add_edge (1, 3);
	GGGG_1.add_edge (3, 4);
    GGGG_1.add_edge (1, 5);
    GGGG_1.add_edge (5, 6);
    GGGG_1.add_edge (5, 7);
    GGGG_1.add_edge (6, 7);

	bipartite bip_graph_1 (GGGG_1);
	cout << "Is bipartite? -> " << bip_graph_1.is_bipartite () << endl;
	cout << "Odd cycle: ";
	auto odd_cycle = bip_graph_1.get_odd_cycle ();
	for (auto w : odd_cycle)
		cout << w << " ";
	cout << endl;
	cout << endl;

	graph GGGG_2 (5);

	GGGG_2.add_edge (0, 1);
	GGGG_2.add_edge (1, 2);
	GGGG_2.add_edge (2, 3);
	GGGG_2.add_edge (0, 3);
	GGGG_2.add_edge (3, 4);

	bipartite bip_graph_2 (GGGG_2);
	cout << "Is bipartite? -> " << bip_graph_2.is_bipartite () << endl;
	cout << "Part V: ";
	auto V = bip_graph_2.get_V ();
	for (auto w : V)
		cout << "(" << w << ", " << bip_graph_2.get_color (w) << ") ";
	cout << endl;
	cout << "Part U: ";
	auto U = bip_graph_2.get_U ();
	for (auto w : U)
		cout << "(" << w << ", " << bip_graph_2.get_color (w) << ") ";
	cout << endl;
	cout << endl;

	return 0;
}
