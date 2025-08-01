/**
 * OpenAPI Petstore
 * This spec is mainly for testing Petstore server and contains fake endpoints, models. Please do not use this for any other purpose. Special characters: \" \\
 *
 * The version of the OpenAPI document: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by OpenAPI-Generator 7.15.0-SNAPSHOT.
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */

/*
 * List.h
 *
 * 
 */

#ifndef List_H_
#define List_H_



#include <string>
#include <memory>
#include <vector>
#include <boost/property_tree/ptree.hpp>
#include "helpers.h"

namespace org {
namespace openapitools {
namespace server {
namespace model {

/// <summary>
/// 
/// </summary>
class  List 
{
public:
    List() = default;
    explicit List(boost::property_tree::ptree const& pt);
    virtual ~List() = default;

    List(const List& other) = default; // copy constructor
    List(List&& other) noexcept = default; // move constructor

    List& operator=(const List& other) = default; // copy assignment
    List& operator=(List&& other) noexcept = default; // move assignment

    std::string toJsonString(bool prettyJson = false) const;
    void fromJsonString(std::string const& jsonString);
    boost::property_tree::ptree toPropertyTree() const;
    void fromPropertyTree(boost::property_tree::ptree const& pt);


    /////////////////////////////////////////////
    /// List members

    /// <summary>
    /// 
    /// </summary>
    std::string getR123List() const;
    void setR123List(std::string value);

protected:
    std::string m_r_123_list = "";
};

std::vector<List> createListVectorFromJsonString(const std::string& json);

template<>
inline boost::property_tree::ptree toPt<List>(const List& val) {
    return val.toPropertyTree();
}

template<>
inline List fromPt<List>(const boost::property_tree::ptree& pt) {
    List ret;
    ret.fromPropertyTree(pt);
    return ret;
}

}
}
}
}

#endif /* List_H_ */
