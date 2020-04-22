d_objs = D.objects
parent_obj = d_objs.new(mesh_entity["name"], None)
tr = mesh_entity["transform"]
parent_obj.location = tr["location"]
parent_obj.rotation_mode = tr["rotation_mode"]
parent_obj.rotation_quaternion = tr["rotation_quaternion"]
parent_obj.scale = tr["scale"]
parent_obj.empty_display_size = 0.25
scene_objs = C.scene.collection.objects
scene_objs.link(parent_obj)

materials = mesh_entity["materials"]
d_mats = D.materials
for material in materials:
    fill_clr = material["fill"]
    metal_val = material["metallic"]
    rough_val = material["roughness"]

    mat_data = d_mats.new(material["name"])
    mat_data.diffuse_color = fill_clr
    mat_data.metallic = metal_val
    mat_data.roughness = rough_val
    mat_data.use_nodes = True

    node_tree = mat_data.node_tree
    nodes = node_tree.nodes
    pbr = nodes["Principled BSDF"]
    pbr_in = pbr.inputs
    pbr_in["Base Color"].default_value = fill_clr
    pbr_in["Metallic"].default_value = metal_val
    pbr_in["Roughness"].default_value = rough_val
    specular = pbr_in["Specular"]
    specular.default_value = material["specular"]
    clearcoat = pbr_in["Clearcoat"]
    clearcoat.default_value = material["clearcoat"]
    cr = pbr_in["Clearcoat Roughness"]
    cr.default_value = material["clearcoat_roughness"]
    
meshes = mesh_entity["meshes"]
d_meshes = D.meshes
for mesh in meshes:
    name = mesh["name"]
    vert_dat = mesh["vertices"]
    fc_idcs = mesh["faces"]
    mesh_data = d_meshes.new(name)
    mesh_data.from_pydata(vert_dat, [], fc_idcs)
    mesh_data.validate()