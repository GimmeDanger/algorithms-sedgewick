#pragma once

// digraph.h

#include <vector>
#include <stdexcept>

/**
 * @brief Datatype represents directed graph
 */
class digraph
{
private:
  int vertexes_num = 0;
  int edges_num = 0;
  std::vector<std::vector<int> > adjacency_list;
  std::vector<int> in_degree;

  /**
   * @brief Validate vertex being in range [0, vertexes_num - 1]
   * 
   * @param v a vertex
   * @throws std::invalid_argument exception unless v is valid
   */
  void validate_vertex (int v) const
    {
      if (v < 0 || v >= vertexes_num)
        throw std::invalid_argument ("Graph vertex must be in range [0, vertexes_num-1].");
    }

public:
  digraph () = delete;

  digraph (unsigned int m_vertexes_num)
    {
      vertexes_num = m_vertexes_num;
      edges_num = 0;
      adjacency_list.resize (vertexes_num);
      in_degree.resize (vertexes_num, 0);
    }

  int get_vertexex_num () const { return vertexes_num; }
  int get_edges_num () const { return edges_num; }

  void add_vertex ()
  {
    vertexes_num++;
    adjacency_list.push_back (std::vector<int> ());
  }

  /**
   * Adds the directed edge v→w to this digraph.
   *
   * @param  v the tail vertex
   * @param  w the head vertex
   * @throws std::invalid_argument exception unless both v and w are in range
   */
  void add_edge (int v, int w)
    {
      validate_vertex (v);
      validate_vertex (w);      
      adjacency_list[v].push_back (w);
      in_degree[w]++;
      edges_num++;
    }

  const std::vector<int> & adj (int v) const
    {
      validate_vertex (v);
      return adjacency_list[v];
    }

  int get_out_degree (int v) const
    {
      validate_vertex (v);
      return adj (v).size ();
    }

  int get_in_degree (int v) const
    {
      validate_vertex (v);
      return in_degree[v];
    }
};