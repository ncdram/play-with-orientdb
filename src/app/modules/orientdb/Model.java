package modules.orientdb;

import java.util.List;

import com.orientechnologies.orient.core.exception.ORecordNotFoundException;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.core.iterator.object.OObjectIteratorClassInterface;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public abstract class Model {
    /**
     * Prepare a query to find *all* entities.
     * 
     * @return An OObjectIterator
     */
    public static <T extends Model> OObjectIteratorClassInterface<T> all() {
        throw new UnsupportedOperationException("Model not enhanced.");
    }

    /**
     * Count entities
     * 
     * @return number of entities of this class
     */
    public static long count() {
        throw new UnsupportedOperationException("Model not enhanced.");
    }

    /**
     * Use this for multiple DB operations
     * Don't forget to .close() at the end.
     * @return
     */
    public static OObjectDatabaseTx  db() {
        return ODB.openObjectDB();
    }

    /**
     * This creates a proxied object istance
     * @param clazz
     * @return
     */
    public static <T> T newInstance(Class<T> clazz) {
    	return db().newInstance(clazz);
    }
    
    public static <T extends Model> T findByOrid(ORID id) {
    	return db().load(id);
    }
    
    /**
     * Prepare a query to find entities.
     * 
     * @param query
     *            OSQL query
     * @param params
     *            Params to bind to the query
     * @return A result set
     */
    public static <T extends Model> List<T> find(String query, Object... params) {
        return db().query(new OSQLSynchQuery<T>(query), params);
    }
    /**
     * Delete all entities
     * 
     * @return Number of entities deleted
     */
    public static int deleteAll() {
        int i = 0;
        for (Model m : all()) {
            m.delete();
            i++;
        }
        return i;
    }
    
    
    //TODO I do not think I need this.
//    @SuppressWarnings({ "unchecked", "rawtypes" })
//    public static <T extends Model> T edit(Object o, String name, 
//    		Map<String, String[]> params, Annotation[] annotations) {
//        try {
//            BeanWrapper bw = new BeanWrapper(o.getClass());
//            // Start with relations
//            Set<Field> fields = new HashSet<Field>();
//            Class clazz = o.getClass();
//            while (!clazz.equals(Object.class)) {
//                Collections.addAll(fields, clazz.getDeclaredFields());
//                clazz = clazz.getSuperclass();
//            }
//            for (Field field : fields) {
//                boolean isEntity = false;
//                String relation = null;
//                boolean multiple = false;
//                //
//                if (Model.class.isAssignableFrom(field.getType())) {
//                    isEntity = true;
//                    relation = field.getType().getName();
//                }
//                if (Collection.class.isAssignableFrom(field.getType())) {
//                    Class fieldType = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
//                    if (Model.class.isAssignableFrom(fieldType)) {
//                        isEntity = true;
//                        relation = fieldType.getName();
//                        multiple = true;
//                    }
//                }
//
//                if (isEntity) {
//                    Class<Model> c = (Class<Model>) Play.classloader.loadClass(relation);
//                    if (Model.class.isAssignableFrom(c)) {
//                        String keyName = Model.Manager.factoryFor(c).keyName();
//                        if (multiple && Collection.class.isAssignableFrom(field.getType())) {
//                            Collection l = new ArrayList();
//                            if (SortedSet.class.isAssignableFrom(field.getType())) {
//                                l = new TreeSet();
//                            } else if (Set.class.isAssignableFrom(field.getType())) {
//                                l = new HashSet();
//                            }
//                            String[] ids = params.get(name + "." + field.getName() + "." + keyName);
//                            if (ids != null) {
//                                params.remove(name + "." + field.getName() + "." + keyName);
//                                for (String _id : ids) {
//                                    if (_id.equals("")) {
//                                        continue;
//                                    }
//                                    Object param = _id;
//                                    try {
//                                        if (param instanceof String) {
//                                            param = new ORecordId((String) param);
//                                        }
//                                        Object res = ODB.openObjectDB().load((ORID) param);
//                                        l.add(res);
//                                    } catch (ORecordNotFoundException e) {
//                                        Validation.addError(name + "." + field.getName(), "validation.notFound", _id);
//                                    }
//                                }
//                                bw.set(field.getName(), o, l);
//                            }
//                        } else {
//                            String[] ids = params.get(name + "." + field.getName() + "." + keyName);
//                            if (ids != null && ids.length > 0 && !ids[0].equals("")) {
//                                params.remove(name + "." + field.getName() + "." + keyName);
//                                Object param = ids[0];
//                                try {
//                                    String localName = name + "." + field.getName();
//                                    if (param instanceof String) {
//                                        param = new ORecordId((String) param);
//                                    }
//                                    Object to = ODB.openObjectDB().load((ORID) param);
//                                    edit(to, localName, params, field.getAnnotations());
//                                    params = Utils.filterMap(params, localName);
//                                    bw.set(field.getName(), o, to);
//                                } catch (ORecordNotFoundException e) {
//                                    Validation.addError(name + "." + field.getName(), "validation.notFound", ids[0]);
//                                }
//                            } else if (ids != null && ids.length > 0 && ids[0].equals("")) {
//                                bw.set(field.getName(), o, null);
//                                params.remove(name + "." + field.getName() + "." + keyName);
//                            }
//                        }
//                    }
//                }
//            }
//            bw.bind(name, o.getClass(), params, "", o, annotations);
//            return (T) o;
//        } catch (Exception e) {
//        	throw new UnexpectedException(Option.apply(e.getMessage()), Option.apply(e.getCause()));
//        }
//    }

    /**
     * Find the entity with the corresponding id.
     * 
     * @param id
     *            The entity id
     * @return The entity
     */
    @SuppressWarnings("unchecked")
    public static <T extends Model> T findById(ORID id) {
        try {
            return (T) db().load(id);
        } catch (ORecordNotFoundException e) {
            return null;
        }
    }

    public void _delete() {
        db().delete(this);
    }

    public ORID _key() {
    	return db().getIdentity(this);
    }
    
    /**
     * Delete the entity.
     * 
     * @return The deleted entity.
     */
    @SuppressWarnings("unchecked")
    public <T extends Model> T delete() {
        _delete();
        return (T) this;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if ((this == other)) {
            return true;
        }
        if (!this.getClass().isAssignableFrom(other.getClass())) {
            return false;
        }
        if (this._key() == null) {
            return false;
        }
        return this._key().equals(((Model) other)._key());
    }

    public Object getIdentity() {
        return _key();
    }

    @Override
    public int hashCode() {
        if (this._key() == null) {
            return 0;
        }
        return this._key().hashCode();
    }

    public boolean isManaged() {
        return db().isManaged(this);
    }

    /**
     * Refresh the entity state.
     */
    @SuppressWarnings("unchecked")
    public <T extends Model> T refresh() {
        db().reload(this);
        return (T) this;
    }

    /**
     * store (ie insert) the entity.
     */
    @SuppressWarnings("unchecked")
    public <T extends Model> T save() {
        return (T) db().save(this);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + _key() + "]";
    }

    
    // TODO Finish this
//    public boolean validateAndSave() {
//    	
//        if (Validation.getValidator().) {
//            save();
//            return true;
//        }
//        return false;
//    }

}
